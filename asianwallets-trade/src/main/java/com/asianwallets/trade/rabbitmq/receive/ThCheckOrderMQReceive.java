package com.asianwallets.trade.rabbitmq.receive;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.dto.th.ISO8583.ThDTO;
import com.asianwallets.common.dto.th.ISO8583.TlvUtil;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.RabbitMassage;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.ThCheckOrderQueueDTO;
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
 * @description: 通华查询队列
 **/
@Component
@Slf4j
public class ThCheckOrderMQReceive {

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

    @RabbitListener(queues = "MQ_TH_CHECK_ORDER")
    public void thCheckOrderMQ(String value) {
        RabbitMassage message = JSON.parseObject(value, RabbitMassage.class);
        if (message.getCount() > 0) {
            //请求次数减一
            message.setCount(message.getCount() - 1);
            log.info("==============【Th查询订单队列】============== value: {}", value);
            ThCheckOrderQueueDTO thCheckOrderQueueDTO = JSON.parseObject(message.getValue(), ThCheckOrderQueueDTO.class);
            Orders orders = thCheckOrderQueueDTO.getOrders();
            ISO8583DTO iso8583DTO = thCheckOrderQueueDTO.getIso8583DTO();
            iso8583DTO.setProcessingCode_3("700206");
            iso8583DTO.setPointOfServiceEntryMode_22("000");
            //附加信息
            String domain46 = "3030020202" + orders.getChannelNumber() + "0202";
            iso8583DTO.setAdditionalData_46(TlvUtil.tlv5f52(domain46));
            ThDTO thDTO = new ThDTO();
            thDTO.setIso8583DTO(iso8583DTO);
            thDTO.setChannel(thCheckOrderQueueDTO.getChannel());
            log.info("==============【Th查询订单队列】============== 【查询请求参数】 thDTO: {}", JSON.toJSONString(thDTO));
            BaseResponse channelResponse = channelsFeign.thQuery(thDTO);
            log.info("==============【Th查询订单队列】============== 【查询响应参数】 response: {}", JSON.toJSONString(channelResponse));
            if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
                log.info("==============【Th查询订单队列】==============【订单查询异常,上报队列】【E_MQ_TH_CHECK_ORDER】");
                rabbitMQSender.send(AD3MQConstant.E_MQ_TH_CHECK_ORDER, JSON.toJSONString(message));
                return;
            }
            ISO8583DTO iso8583VO = JSON.parseObject(JSON.toJSONString(channelResponse.getData()), ISO8583DTO.class);
            log.info("==================【Th查询订单队列】==================【调用Channels服务】【通华查询接口解析结果】  iso8583VO: {}", JSON.toJSONString(iso8583VO));
            if (!"00".equals(iso8583VO.getResponseCode_39())) {
                log.info("==============【Th查询订单队列】==============【查询接口返回异常】");
                return;
            }
            orders.setChannelCallbackTime(new Date());
            orders.setUpdateTime(new Date());
            Example example = new Example(Orders.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("tradeStatus", "2");
            criteria.andEqualTo("id", orders.getId());
            if ("1".equals(iso8583VO.getAdditionalData_46())) {
                log.info("=================【Th查询订单队列】=================【订单已支付成功】 orderId: {}", orders.getId());
                //未发货
                orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
                //未签收
                orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
                orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
                try {
                    channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
                } catch (Exception e) {
                    log.error("=================【Th查询订单队列】=================【更新通道订单异常】", e);
                }
                //更新订单信息
                if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                    log.info("=================【Th查询订单队列】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                    //计算支付成功时的通道网关手续费
                    commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                    //TODO 添加日交易限额与日交易笔数
                    //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                    //支付成功后向用户发送邮件
                    commonBusinessService.sendEmail(orders);
                    try {
                        //账户信息不存在的场合创建对应的账户信息
                        if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                            log.info("=================【Th查询订单队列】=================【上报清结算前线下下单创建账户信息】");
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
                            log.info("=================【Th查询订单队列】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                            rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                        }
                    } catch (Exception e) {
                        log.error("=================【Th查询订单队列】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } else {
                    log.info("=================【Th查询订单队列】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
                }
            } else if ("5".equals(iso8583VO.getAdditionalData_46()) || "4".equals(iso8583VO.getAdditionalData_46())) {
                log.info("=================【Th查询订单队列】=================【订单已支付失败】 orderId: {}", orders.getId());
                orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                orders.setRemark5(iso8583VO.getAdditionalData_46());
                try {
                    channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
                } catch (Exception e) {
                    log.error("=================【Th查询订单队列】=================【更新通道订单异常】", e);
                }
                //计算支付失败时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeFailed(orders);
                if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                    log.info("=================【Th查询订单队列】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
                } else {
                    log.info("=================【Th查询订单队列】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
                }
            } else if ("2".equals(iso8583VO.getAdditionalData_46())) {
                log.info("==============【Th查询订单队列】============== 【订单状态为2,转入退款或者已撤销或者交易超时】 orderID : {}", orders.getId());
                return;
            } else {
                log.info("==============【Th查询订单队列】============== 【订单是交易中】 orderID : {}", orders.getId());
                log.info("==============【Th查询订单队列】============== 【继续上报Th查询订单队列】 【E_MQ_TH_CHECK_ORDER】");
                rabbitMQSender.send(AD3MQConstant.E_MQ_TH_CHECK_ORDER, JSON.toJSONString(message));
                return;
            }
            try {
                //商户服务器回调地址不为空,回调商户服务器
                if (!StringUtils.isEmpty(orders.getServerUrl())) {
                    commonBusinessService.replyReturnUrl(orders);
                }
            } catch (Exception e) {
                log.info("=================【Th查询订单队列】=================【回调商户异常】", e);
            }
        } else {
            log.error("==============【Th查询订单队列】============== 【90秒查询,订单为交易中】 message : {}", JSON.toJSONString(message));
        }
    }
}
