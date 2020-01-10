package com.asianwallets.trade.rabbitmq.receive;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.megapay.MegaPayQueryDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.feign.MessageFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.vo.FundChangeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @description: MegaPay-THB通道查询队列2
 * @author: XuWenQi
 * @create: 2019-11-21 11:23
 **/
@Component
@Slf4j
public class MegaPayQueryMQReceiveSecond {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private MessageFeign messageFeign;

    @RabbitListener(queues = "MQ_MEGAPAY_THB_CHECK_ORDER2")
    public void megaPayQuery(String value) {
        RabbitMassage rabbitMassage = JSON.parseObject(value, RabbitMassage.class);
        JSONObject jsonObject = JSONObject.parseObject(rabbitMassage.getValue());
        //订单号
        String orderId = jsonObject.getString("orderId");
        //商户号
        String channelMerchantId = jsonObject.getString("channelMerchantId");
        if (rabbitMassage.getCount() > 0) {
            //请求次数减一
            rabbitMassage.setCount(rabbitMassage.getCount() - 1);
            log.info("==============【MegaPay-THB查询队列2】============== value: {}", value);
            MegaPayQueryDTO megaPayQueryDTO = new MegaPayQueryDTO();
            megaPayQueryDTO.setInvoice(orderId);
            megaPayQueryDTO.setMerchantID(channelMerchantId);
            BaseResponse response = channelsFeign.megaPayQuery(megaPayQueryDTO);
            log.info("==============【MegaPay-THB查询队列2】==============【Channels服务MegaPay-THB接口响应参数】 response: {}", JSON.toJSONString(response));
            if (!TradeConstant.HTTP_SUCCESS.equals(response.getCode())) {
                log.info("==============【MegaPay-THB查询队列2】==============【接口响应错误 继续上报MegaPay-THB查询订单队列2】");
                rabbitMQSender.send(AD3MQConstant.E_MQ_MEGAPAY_THB_CHECK_ORDER2, JSON.toJSONString(rabbitMassage));
                return;
            }
            //查询原订单信息
            Orders orders = ordersMapper.selectByPrimaryKey(orderId);
            if (orders == null) {
                log.info("==============【MegaPay-THB查询队列2】==============【查询订单信息不存在】 orderId :{}", orderId);
                return;
            }
            //订单已支付
            if (!orders.getTradeStatus().equals(TradeConstant.ORDER_PAYING)) {
                log.info("==============【MegaPay-THB查询队列2】==============【订单状态不为支付中】");
                return;
            }
            //订单状态
            String result = String.valueOf(response.getData());
            //通道回调时间
            orders.setChannelCallbackTime(new Date());
            //修改时间
            orders.setUpdateTime(new Date());
            Example example = new Example(Orders.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("tradeStatus", "2");
            criteria.andEqualTo("id", orders.getId());
            if ("1".equals(result)) {
                log.info("==============【MegaPay-THB查询队列2】============== 【订单已支付成功】 orderID : {}", orders.getId());
                //未发货
                orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
                //未签收
                orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
                //支付成功
                orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
                //更改channelsOrders状态
                try {
                    channelsOrderMapper.updateStatusById(orderId, null, TradeConstant.TRADE_SUCCESS);
                } catch (Exception e) {
                    log.error("==============【MegaPay-THB查询队列2】============== 【更新通道订单异常】", e);
                }
                //修改订单状态
                int i = ordersMapper.updateByExampleSelective(orders, example);
                if (i > 0) {
                    log.info("==============【MegaPay-THB查询队列2】============== 【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                    //计算支付成功时的通道网关手续费
                    commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                    //TODO 添加日交易限额与日交易笔数
                    //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                    //支付成功后向用户发送邮件
                    commonBusinessService.sendEmail(orders);
                    try {
                        //账户信息不存在的场合创建对应的账户信息
                        if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                            log.info("==============【MegaPay-THB查询队列2】============== 【上报清结算前线下下单创建账户信息】");
                            commonBusinessService.createAccount(orders);
                        }
                        //分润
                        if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                            rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                        }
                        //更新成功,上报清结算
                        FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                        //上报清结算资金变动接口
                        BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                        if (fundChangeResponse.getCode() != null && TradeConstant.HTTP_SUCCESS.equals(fundChangeResponse.getCode())) {
                            //请求成功
                            FundChangeVO fundChangeVO = (FundChangeVO) fundChangeResponse.getData();
                            if (!fundChangeVO.getRespCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                                //业务处理失败
                                log.info("==============【MegaPay-THB查询队列2】============== 【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                                RabbitMassage massage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                                rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(massage));
                            }
                        } else {
                            log.info("==============【MegaPay-THB查询队列2】============== 【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                            RabbitMassage massage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                            rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(massage));
                        }
                    } catch (Exception e) {
                        log.error("==============【MegaPay-THB查询队列2】============== 【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                        RabbitMassage massage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(massage));
                    }
                } else {
                    log.info("==============【MegaPay-THB查询队列2】============== 【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
                }
            } else if ("9".equals(result)) {
                log.info("==============【MegaPay-THB查询队列2】============== 订单已支付失败 orderID : {}", orders.getId());
                //支付失败
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                //更改channelsOrders状态
                try {
                    channelsOrderMapper.updateStatusById(orderId, null, TradeConstant.TRADE_FALID);
                } catch (Exception e) {
                    log.info("==============【MegaPay-THB查询队列2】============== 【更新通道订单异常】", e);
                }
                //计算支付失败时通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeFailed(orders);
                if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                    log.info("=================【NL查询队列】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
                } else {
                    log.info("==============【MegaPay-THB查询队列2】============== 【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
                }
            } else {
                log.info("==============【MegaPay-THB查询队列2】============== 【订单是交易中】 orderID : {}", orders.getId());
                log.info("==============【MegaPay-THB查询队列2】============== 【继续上报查询订单队列2】 【E_MQ_MEGAPAY_THB_CHECK_ORDER2】");
                rabbitMQSender.send(AD3MQConstant.E_MQ_MEGAPAY_THB_CHECK_ORDER2, JSON.toJSONString(rabbitMassage));
                return;
            }
            //回调商户
            try {
                if (!StringUtils.isEmpty(orders.getServerUrl())) {
                    commonBusinessService.replyReturnUrl(orders);
                }
                if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                    commonBusinessService.replyBrowserUrl(orders);
                }
            } catch (Exception e) {
                log.info("==============【MegaPay-THB查询队列2】==============【回调商户异常】", e);
            }
        } else {
            log.info("==============【MegaPay-THB查询队列2】============== 【二次查询,订单为交易中】 rabbitMassage : {}", JSON.toJSONString(rabbitMassage));
        }
    }
}
