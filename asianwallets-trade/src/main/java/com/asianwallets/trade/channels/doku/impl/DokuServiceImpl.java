package com.asianwallets.trade.channels.doku.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.doku.DOKURefundDTO;
import com.asianwallets.common.dto.doku.DOKUReqDTO;
import com.asianwallets.common.dto.doku.DOKURequestDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.doku.DokuService;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-03-19 15:57
 **/
@Slf4j
@Service
@Transactional
@HandlerType(TradeConstant.DOKU)
public class DokuServiceImpl extends ChannelsAbstractAdapter implements DokuService {

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {

        BaseResponse baseResponse = new BaseResponse();
        DOKURefundDTO dokuRequestDTO = new DOKURefundDTO(orderRefund, channel);
        DOKUReqDTO dokuReqDTO = new DOKUReqDTO();
        dokuReqDTO.setDokuRefundDTO(dokuRequestDTO);
        dokuReqDTO.setKey(channel.getMd5KeyStr());

        log.info("============【DOKU退款】============【请求参数】 dokuReqDTO: {}", JSON.toJSONString(dokuReqDTO));
        BaseResponse response = channelsFeign.dokuRefund(dokuReqDTO);
        log.info("============【DOKU退款】============【响应参数】 baseResponse: {}", JSON.toJSONString(response));
        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {

            //请求成功
            if (response.getMsg().equals(TradeConstant.HTTP_SUCCESS_MSG)) {
                //退款成功
                Map<String, String> respMap = (Map<String, String>) response.getData();
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【DOKU退款】=================【退款成功】 response: {} ", JSON.toJSONString(response));
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, String.valueOf(respMap.get("REFNUM")), null);
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
            }else{
                //退款失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【DOKU退款】=================【退款失败】 response: {} ", JSON.toJSONString(response));
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                String type = orderRefund.getRemark4().equals(TradeConstant.RF) ? TradeConstant.AA : TradeConstant.RA;
                String reconciliationRemark = type.equals(TradeConstant.AA) ? TradeConstant.REFUND_FAIL_RECONCILIATION : TradeConstant.CANCEL_ORDER_REFUND_FAIL;
                Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, reconciliationRemark);
                reconciliationMapper.insert(reconciliation);
                FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                log.info("=========================【DOKU退款】======================= 【调账 {}】， fundChangeDTO:【{}】", type, JSON.toJSONString(fundChangeDTO));
                BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                    //调账成功
                    log.info("=================【DOKU退款】=================【调账成功】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                    reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundFail(orderRefund);
                } else {
                    //调账失败
                    log.info("=================【DOKU退款】=================【调账失败】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    RabbitMassage rabbitMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                    log.info("=================【DOKU退款】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                    rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
                }
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【DOKU退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.TK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }

    @Override
    public BaseResponse cancel(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
        if (rabbitMassage == null) {
            rabbitMassage = rabbitOrderMsg;
        }
        BaseResponse response = new BaseResponse();
        DOKURequestDTO dokuRequestDTO = new DOKURequestDTO(orderRefund, channel);
        DOKUReqDTO dokuReqDTO = new DOKUReqDTO();
        dokuReqDTO.setDokuRequestDTO(dokuRequestDTO);
        dokuReqDTO.setKey(channel.getMd5KeyStr());
        log.info("============【DOKU撤销】============【请求参数】 dokuReqDTO: {}", JSON.toJSONString(dokuReqDTO));
        BaseResponse baseResponse = channelsFeign.checkStatus(dokuReqDTO);
        log.info("============【DOKU撤销】============【响应参数】 baseResponse: {}", JSON.toJSONString(response));
        if (baseResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            if (response.getMsg().equals(TradeConstant.HTTP_SUCCESS_MSG)) {
                Map<String, String> respMap = (Map<String, String>) response.getData();
                //交易成功
                //更新订单状态
                if (ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_SUCCESS, null, new Date()) == 1) {
                    //更新成功
                    response = this.cancelPaying(channel, orderRefund, null);
                } else {
                    response.setCode(EResultEnum.REFUNDING.getCode());
                    //更新失败后去查询订单信息
                    rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
                }
            }else{
                //其他状态
                response.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【NextPos撤销】================= 【其他状态】orderId : {}", orderRefund.getOrderId());
            }
        }else{
            //请求失败
            //请求失败
            response.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【NextPos撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
            rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return response;
    }

    @Override
    public BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        DOKURefundDTO dokuRequestDTO = new DOKURefundDTO(orderRefund, channel);
        DOKUReqDTO dokuReqDTO = new DOKUReqDTO();
        dokuReqDTO.setDokuRefundDTO(dokuRequestDTO);
        dokuReqDTO.setKey(channel.getMd5KeyStr());
        log.info("============【DOKU撤销 cancelPaying】============【请求参数】 dokuReqDTO: {}", JSON.toJSONString(dokuReqDTO));
        BaseResponse response = channelsFeign.dokuRefund(dokuReqDTO);
        log.info("============【DOKU撤销 cancelPaying】============【响应参数】 baseResponse: {}", JSON.toJSONString(response));
        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            if (response.getMsg().equals(TradeConstant.HTTP_SUCCESS_MSG)) {
                //撤销成功
                Map<String, String> respMap = (Map<String, String>) response.getData();
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【DOKU撤销 cancelPaying】=================【撤销成功】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
            }else{
                //撤销失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【DOKU撤销 cancelPaying】=================【撤销失败】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            }

        }else{
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【DOKU撤销 cancelPaying】=================【请求失败】orderId : {}", orderRefund.getOrderId());
            RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            if (rabbitMassage == null) {
                rabbitMassage = rabbitOrderMsg;
            }
            log.info("=================【DOKU撤销 cancelPaying】=================【上报通道】rabbitMassage: {} ", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.CX_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }
}
