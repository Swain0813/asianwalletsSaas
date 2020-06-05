package com.asianwallets.trade.rabbitmq.receive;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.qfpay.QfPayDTO;
import com.asianwallets.common.dto.qfpay.QfPayQueryDTO;
import com.asianwallets.common.dto.qfpay.QfResDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @description: QfPay-csb查询队列
 **/
@Component
@Slf4j
public class QfPayCsbCheckOrderMQRecive {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @RabbitListener(queues = "MQ_QFPAY_CSB_CHECK_ORDER")
    public void qfPayCsbCheckOrderMQ(String value) {
        RabbitMassage message = JSON.parseObject(value, RabbitMassage.class);
        QfPayDTO qfPayDTO = JSON.parseObject(message.getValue(), QfPayDTO.class);
        if (message.getCount() > 0) {
            //请求次数减一
            message.setCount(message.getCount() - 1);
            log.info("==============【QfPay-Csb查询订单队列】============== value: {}", value);
            QfPayQueryDTO qfPayQueryDTO = qfPayDTO.getQfPayQueryDTO();
            //查询原订单信息
            Orders orders = ordersMapper.selectByPrimaryKey(qfPayQueryDTO.getOutTradeNo());
            if (orders == null) {
                log.info("==============【QfPay-Csb查询订单队列】==============【订单不存在】");
                return;
            }
            //订单已支付
            if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
                log.info("==============【QfPay-Csb查询订单队列】==============【订单状态不为支付中】status :{}", orders.getTradeStatus());
                return;
            }
            log.info("==============【QfPay-Csb查询订单队列】============== 【查询请求参数】 qfPayDTO: {}", JSON.toJSONString(qfPayDTO));
            BaseResponse response = channelsFeign.qfPayQuery(qfPayDTO);
            log.info("==============【QfPay-Csb查询订单队列】============== 【查询响应参数】 response: {}", JSON.toJSONString(response));
            if (!TradeConstant.HTTP_SUCCESS.equals(response.getCode())) {
                rabbitMQSender.send(AD3MQConstant.E_MQ_QFPAY_CSB_CHECK_ORDER, JSON.toJSONString(message));
                log.info("==============【QfPay-Csb查询订单队列】==============【订单查询异常】");
                return;
            }
            QfResDTO qfResDTO = JSON.parseObject(JSON.toJSONString(response.getData()), QfResDTO.class);
            orders.setChannelCallbackTime(new Date());
            orders.setUpdateTime(new Date());
            Example example = new Example(Orders.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("tradeStatus", "2");
            criteria.andEqualTo("id", orders.getId());
            if ("0000".equals(qfResDTO.getStatus())) {
                log.info("=================【QfPay-Csb查询订单队列】=================【订单已支付成功】 orderId: {}", orders.getId());
                //未发货
                orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
                //未签收
                orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
                orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
                try {
                    channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
                } catch (Exception e) {
                    log.error("=================【QfPay-Csb查询订单队列】=================【更新通道订单异常】", e);
                }
                //更新订单信息
                if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                    log.info("=================【QfPay-Csb查询订单队列】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                    //计算支付成功时的通道网关手续费
                    commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                    //TODO 添加日交易限额与日交易笔数
                    //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                    //支付成功后向用户发送邮件
                    commonBusinessService.sendEmail(orders);
                    try {
                        //账户信息不存在的场合创建对应的账户信息
                        if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                            log.info("=================【QfPay-Csb查询订单队列】=================【上报清结算前线下下单创建账户信息】");
                            commonBusinessService.createAccount(orders);
                        }
                        //分润
                        if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                            rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                        }
                        FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                        //上报清结算资金变动接口
                        BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                        if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                            log.info("=================【QfPay-Csb查询订单队列】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                            rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                        }
                    } catch (Exception e) {
                        log.error("=================【QfPay-Csb查询订单队列】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } else {
                    log.info("=================【QfPay-Csb查询订单队列】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
                }
            } else if ("1143".equals(qfResDTO.getStatus())) {
                log.info("==============【QfPay-Csb查询订单队列】============== 【订单是交易中】 orderID : {}", orders.getId());
                log.info("==============【QfPay-Csb查询订单队列】============== 【继续上报QfPay-CSB查询订单队列】 【MQ_QFPAY_CSB_CHECK_ORDER-CSB】");
                rabbitMQSender.send(AD3MQConstant.E_MQ_QFPAY_CSB_CHECK_ORDER, JSON.toJSONString(message));
                return;
            } else {
                log.info("=================【QfPay-Csb查询订单队列】=================【订单已支付失败】 orderId: {}", orders.getId());
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                orders.setRemark5(qfResDTO.getStatus());
                try {
                    channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
                } catch (Exception e) {
                    log.error("=================【QfPay-Csb查询订单队列】=================【更新通道订单异常】", e);
                }
                //计算支付失败时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeFailed(orders);
                if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                    log.info("=================【QfPay-Csb查询订单队列】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
                } else {
                    log.info("=================【QfPay-Csb查询订单队列】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
                }
            }
            try {
                //商户服务器回调地址不为空,回调商户服务器
                if (!StringUtils.isEmpty(orders.getServerUrl())) {
                    commonBusinessService.replyReturnUrl(orders);
                }
            } catch (Exception e) {
                log.error("=================【QfPay-Csb查询订单队列】=================【回调商户异常】", e);
            }
        } else {
            log.info("==============【QfPay-Csb查询订单队列】============== 【五次查询,订单为交易中】 message : {}", JSON.toJSONString(message));
        }
    }
}
