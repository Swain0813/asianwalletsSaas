package com.asianwallets.trade.channels.qfpay.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.qfpay.QfPayDTO;
import com.asianwallets.common.dto.qfpay.QfPayQueryDTO;
import com.asianwallets.common.dto.qfpay.QfPayRefundDTO;
import com.asianwallets.common.dto.qfpay.QfResDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.qfpay.QfPayService;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
@Slf4j
@Transactional
@HandlerType(TradeConstant.QFPAY)
public class QfPayServiceImpl extends ChannelsAbstractAdapter implements QfPayService {

    @Autowired
    private ChannelsFeign channelsFeign;
    @Autowired
    private RabbitMQSender rabbitMQSender;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderRefundMapper orderRefundMapper;
    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private ReconciliationMapper reconciliationMapper;
    @Autowired
    private ClearingService clearingService;


    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        return null;
    }

    @Override
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode) {
        return null;
    }

    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        return null;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     **/
    @Override
    public BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        QfPayDTO qfPayDTO = new QfPayDTO();
        qfPayDTO.setChannel(channel);
        qfPayDTO.setReqIp(orderRefund.getReqIp());
        QfPayRefundDTO qfPayRefundDTO = new QfPayRefundDTO(channel, orderRefund);
        qfPayDTO.setQfPayRefundDTO(qfPayRefundDTO);
        log.info("=================【QfPay退款】=================【请求Channels服务QfPay退款】请求参数 qfPayDTO: {} ", JSON.toJSONString(qfPayDTO));
        BaseResponse response = channelsFeign.qfPayRefund(qfPayDTO);
        log.info("=================【QfPay退款】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            JSONObject jsonObject = JSONObject.fromObject(response.getData());
            QfResDTO qfResDTO = JSON.parseObject(String.valueOf(jsonObject), QfResDTO.class);
            if ("0000".equals(qfResDTO.getStatus())) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【QfPay退款】=================【退款成功】 response: {} ", JSON.toJSONString(response));
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS,qfResDTO.getOrderNum(), null);
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);
            } else if ("1143".equals(qfResDTO.getStatus()) || "1145".equals(qfResDTO.getStatus())) {
                //退款中
                //TODO 

            }else{
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【QfPay退款】=================【退款失败】 response: {} ", JSON.toJSONString(response));
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                String type = orderRefund.getRemark4().equals(TradeConstant.RF) ? TradeConstant.AA : TradeConstant.RA;
                String reconciliationRemark = type.equals(TradeConstant.AA) ? TradeConstant.REFUND_FAIL_RECONCILIATION : TradeConstant.CANCEL_ORDER_REFUND_FAIL;
                Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, reconciliationRemark);
                reconciliationMapper.insert(reconciliation);
                FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                log.info("=========================【QfPay退款】======================= 【调账 {}】， fundChangeDTO:【{}】", type, JSON.toJSONString(fundChangeDTO));
                BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                    //调账成功
                    log.info("=================【QfPay退款】=================【调账成功】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                    reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundFail(orderRefund);
                } else {
                    //调账失败
                    log.info("=================【QfPay退款】=================【调账失败】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    RabbitMassage rabbitMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                    log.info("=================【QfPay退款】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                    rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
                }
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【QfPay退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.TK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return response;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate 撤销
     **/
    @Override
    public BaseResponse cancel(Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
        if (rabbitMassage == null) {
            rabbitMassage = rabbitOrderMsg;
        }
        BaseResponse response = new BaseResponse();
        QfPayDTO qfPayDTO = new QfPayDTO();
        qfPayDTO.setChannel(channel);
        qfPayDTO.setReqIp(orderRefund.getReqIp());
        QfPayQueryDTO qfPayQueryDTO = new QfPayQueryDTO(channel, orderRefund);
        qfPayDTO.setQfPayQueryDTO(qfPayQueryDTO);
        log.info("============【QfPay cancel】========【请求Channels服务qfPayQuery】请求参数 qfPayDTO: {} ", JSON.toJSONString(qfPayDTO));
        BaseResponse baseResponse = channelsFeign.qfPayQuery(qfPayDTO);
        log.info("=================【QfPay cancel】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));

        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            JSONObject jsonObject = JSONObject.fromObject(response.getData());
            QfResDTO qfResDTO = JSON.parseObject(String.valueOf(jsonObject), QfResDTO.class);
            if ("0000".equals(qfResDTO.getStatus())) {
                //交易成功
                //更新订单状态
                if (ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_SUCCESS, qfResDTO.getOrderNum(), new Date()) == 1) {
                    //更新成功
                    response = this.cancelPaying(channel, orderRefund, null);
                } else {
                    response.setCode(EResultEnum.REFUNDING.getCode());
                    //更新失败后去查询订单信息
                    rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
                }
            }else if("1143".equals(qfResDTO.getStatus()) || "1145".equals(qfResDTO.getStatus())){
                //交易中

                //TODO
            }else{
                //交易失败
                response.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【QfPay cancel】================= 【交易失败】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_FAILD, qfResDTO.getOrderNum(), new Date());
            }
        }else {
            //请求失败
            response.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【NextPos撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
            rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return response;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate 退款不上报清结算
     **/
    @Override
    public BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage){

        BaseResponse baseResponse = new BaseResponse();
        Orders orders = ordersMapper.selectByPrimaryKey(orderRefund.getOrderId());
        QfPayDTO qfPayDTO = new QfPayDTO();
        qfPayDTO.setChannel(channel);
        qfPayDTO.setReqIp(orders.getReqIp());
        QfPayRefundDTO qfPayRefundDTO = new QfPayRefundDTO(channel, orders);
        qfPayDTO.setQfPayRefundDTO(qfPayRefundDTO);
        log.info("=================【QfPay cancelPaying】=================【请求Channels服务QfPay退款】请求参数 qfPayDTO: {} ", JSON.toJSONString(qfPayDTO));
        BaseResponse response = channelsFeign.qfPayRefund(qfPayDTO);
        log.info("=================【QfPay cancelPaying】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals("200")) {
            //请求成功
            JSONObject jsonObject = JSONObject.fromObject(response.getData());
            QfResDTO qfResDTO = JSON.parseObject(String.valueOf(jsonObject), QfResDTO.class);
            if ("0000".equals(qfResDTO.getStatus())) {
                //撤销成功
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【QfPay cancelPaying】=================【撤销成功】orderId : {}", orders.getId());
                ordersMapper.updateOrderCancelStatus(orders.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
            }else if ("1143".equals(qfResDTO.getStatus()) || "1145".equals(qfResDTO.getStatus())) {
                //退款中
                //TODO

            } else {
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【QfPay cancelPaying】=================【撤销失败】orderId : {}", orders.getId());
                ordersMapper.updateOrderCancelStatus(orders.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            }
        }else{
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【QfPay cancelPaying】=================【请求失败】orderId : {}", orders.getId());
            RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            if (rabbitMassage == null) {
                rabbitMassage = rabbitOrderMsg;
            }
            log.info("=================【QfPay cancelPaying】=================【上报通道】rabbitMassage: {} ", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.CX_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }


}
