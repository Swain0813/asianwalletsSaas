package com.asianwallets.trade.rabbitmq.receive;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.qfpay.QfPayDTO;
import com.asianwallets.common.dto.qfpay.QfPayQueryDTO;
import com.asianwallets.common.dto.qfpay.QfPayRefundSerDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.dao.ChannelMapper;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.feign.MessageFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QfPayMQReceive {
    @Autowired
    private ClearingService clearingService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ChannelsFeign channelsFeign;
    @Autowired
    private OrderRefundMapper orderRefundMapper;
    @Autowired
    private CommonBusinessService commonBusinessService;

    @Value("${custom.warning.mobile}")
    private String developerMobile;

    @Value("${custom.warning.email}")
    private String developerEmail;


    /**
     * 退款中查询对列
     *
     * @param value
     */
    @RabbitListener(queues = "MQ_QFPAY_REFUND_SEARCH")
    public void qFPAYRefundSearch(String value) {
        RabbitMassage rabbitMassage = JSON.parseObject(value, RabbitMassage.class);
        if (rabbitMassage.getCount() > 0) {
            //请求次数减一
            rabbitMassage.setCount(rabbitMassage.getCount() - 1);
            log.info("=================【MQ_QFPAY_REFUND_SEARCH】================= value : {} ", value);
            OrderRefund orderRefund = JSON.parseObject(rabbitMassage.getValue(), OrderRefund.class);
            Channel channel = channelMapper.selectByChannelCode(orderRefund.getChannelCode());

            QfPayDTO qfPayDTO = new QfPayDTO();
            qfPayDTO.setChannel(channel);
            qfPayDTO.setReqIp(orderRefund.getReqIp());
            QfPayQueryDTO qfPayQueryDTO = new QfPayQueryDTO(channel, orderRefund);
            qfPayDTO.setQfPayQueryDTO(qfPayQueryDTO);
            log.info("============【MQ_QFPAY_REFUND_SEARCH】========【请求Channels服务qfPayRefundSearch】请求参数 qfPayDTO: {} ", JSON.toJSONString(qfPayDTO));
            BaseResponse response = channelsFeign.qfPayRefundSearch(qfPayDTO);
            log.info("=================【MQ_QFPAY_REFUND_SEARCH】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
            if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
                JSONObject jsonObject = JSONObject.fromObject(response.getData());
                QfPayRefundSerDTO qfPayRefundSerDTO = JSON.parseObject(String.valueOf(jsonObject), QfPayRefundSerDTO.class);

                if (qfPayRefundSerDTO.getData().size() > 0 && qfPayRefundSerDTO.getData().get(0).getOrderStatus() == 4) {
                    //退款成功
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, qfPayRefundSerDTO.getData().get(0).getOrderNum(), null);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundSuccess(orderRefund);
                    //退还分润
                    commonBusinessService.refundShareBinifit(orderRefund);
                } else if (qfPayRefundSerDTO.getData().size() > 0 && qfPayRefundSerDTO.getData().get(0).getOrderStatus() == 5) {
                    //退款失败
                    log.info("=================【MQ_QFPAY_REFUND_SEARCH】=================【退款失败】 response: {} ", JSON.toJSONString(response));
                    String type = orderRefund.getRemark4().equals(TradeConstant.RF) ? TradeConstant.AA : TradeConstant.RA;
                    String reconciliationRemark = type.equals(TradeConstant.AA) ? TradeConstant.REFUND_FAIL_RECONCILIATION : TradeConstant.CANCEL_ORDER_REFUND_FAIL;
                    Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, reconciliationRemark);
                    reconciliationMapper.insert(reconciliation);
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                    log.info("=========================【MQ_QFPAY_REFUND_SEARCH】======================= 【调账 {}】， fundChangeDTO:【{}】", type, JSON.toJSONString(fundChangeDTO));
                    BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                    if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                        //调账成功
                        log.info("=================【MQ_QFPAY_REFUND_SEARCH】=================【调账成功】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                        orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                        reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                        //改原订单状态
                        commonBusinessService.updateOrderRefundFail(orderRefund);
                    } else {
                        //调账失败
                        log.info("=================【MQ_QFPAY_REFUND_SEARCH】=================【调账失败】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                        com.asianwallets.common.dto.RabbitMassage rabbitMsg = new com.asianwallets.common.dto.RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                        log.info("=================【MQ_QFPAY_REFUND_SEARCH】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                        rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
                    }
                } else {
                    //退款中
                    log.info("===============【MQ_QFPAY_REFUND_SEARCH】===============【退款操作 退款中查询上报队列 E_MQ_QFPAY_REFUND_SEARCH】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
                    rabbitMQSender.send(AD3MQConstant.E_MQ_QFPAY_REFUND_SEARCH, JSON.toJSONString(rabbitMassage));
                }
            } else {
                //请求失败
                log.info("===============【MQ_QFPAY_REFUND_SEARCH】===============【退款操作 退款中查询上报队列 E_MQ_QFPAY_REFUND_SEARCH】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
                rabbitMQSender.send(AD3MQConstant.E_MQ_QFPAY_REFUND_SEARCH, JSON.toJSONString(rabbitMassage));
            }
        } else {
            //预警机制
            messageFeign.sendSimple(developerMobile, "退款中查询对列(QFPAY) MQ_QFPAY_REFUND_SEARCH 预警");//短信通知
            messageFeign.sendSimpleMail(developerEmail, "退款中查询对列(QFPAY) MQ_QFPAY_REFUND_SEARCH 预警", "MQ_QFPAY_REFUND_SEARCH 预警 ：{ " + value + " }");//邮件通知
        }


    }


    /**
     * 撤销退款中查询对列
     *
     * @param value
     */
    @RabbitListener(queues = "MQ_QFPAY_CANNEL_SEARCH")
    public void qFPAYCannelSearch(String value) {

        RabbitMassage rabbitMassage = JSON.parseObject(value, RabbitMassage.class);
        if (rabbitMassage.getCount() > 0) {
            //请求次数减一
            rabbitMassage.setCount(rabbitMassage.getCount() - 1);
            log.info("=================【MQ_QFPAY_CANNEL_SEARCH】================= value : {} ", value);
            OrderRefund orderRefund = JSON.parseObject(rabbitMassage.getValue(), OrderRefund.class);
            Channel channel = channelMapper.selectByChannelCode(orderRefund.getChannelCode());

            QfPayDTO qfPayDTO = new QfPayDTO();
            qfPayDTO.setChannel(channel);
            qfPayDTO.setReqIp(orderRefund.getReqIp());
            QfPayQueryDTO qfPayQueryDTO = new QfPayQueryDTO(channel, orderRefund);
            qfPayDTO.setQfPayQueryDTO(qfPayQueryDTO);
            log.info("============【MQ_QFPAY_CANNEL_SEARCH】========【请求Channels服务qfPayRefundSearch】请求参数 qfPayDTO: {} ", JSON.toJSONString(qfPayDTO));
            BaseResponse response = channelsFeign.qfPayRefundSearch(qfPayDTO);
            log.info("=================【MQ_QFPAY_CANNEL_SEARCH】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
            if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
                JSONObject jsonObject = JSONObject.fromObject(response.getData());
                QfPayRefundSerDTO qfPayRefundSerDTO = JSON.parseObject(String.valueOf(jsonObject), QfPayRefundSerDTO.class);

                if (qfPayRefundSerDTO.getData().size() > 0 && qfPayRefundSerDTO.getData().get(0).getOrderStatus() == 4) {
                    //退款成功
                    log.info("=================【MQ_QFPAY_CANNEL_SEARCH】=================【撤销成功】orderId : {}", orderRefund.getOrderId());
                    ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
                } else if (qfPayRefundSerDTO.getData().size() > 0 && qfPayRefundSerDTO.getData().get(0).getOrderStatus() == 5) {
                    //退款失败
                    log.info("=================【MQ_QFPAY_CANNEL_SEARCH】=================【撤销失败】orderId : {}", orderRefund.getOrderId());
                    ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
                } else {
                    //退款中
                    log.info("===============【MQ_QFPAY_CANNEL_SEARCH】===============【退款操作 退款中查询上报队列 E_MQ_QFPAY_CANNEL_SEARCH】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
                    rabbitMQSender.send(AD3MQConstant.E_MQ_QFPAY_CANNEL_SEARCH, JSON.toJSONString(rabbitMassage));
                }
            } else {
                //请求失败
                log.info("===============【MQ_QFPAY_CANNEL_SEARCH】===============【退款操作 退款中查询上报队列 E_MQ_QFPAY_CANNEL_SEARCH】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
                rabbitMQSender.send(AD3MQConstant.MQ_QFPAY_CANNEL_SEARCH, JSON.toJSONString(rabbitMassage));
            }
        } else {
            //预警机制
            messageFeign.sendSimple(developerMobile, "退款中查询对列(QFPAY) MQ_QFPAY_CANNEL_SEARCH 预警");//短信通知
            messageFeign.sendSimpleMail(developerEmail, "退款中查询对列(QFPAY) MQ_QFPAY_CANNEL_SEARCH 预警", "MQ_QFPAY_CANNEL_SEARCH 预警 ：{ " + value + " }");//邮件通知
        }
    }

}
