package com.asianwallets.trade.channels.alipay.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.alipay.AliPayQueryDTO;
import com.asianwallets.common.dto.alipay.AliPayRefundDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.alipay.AlipayService;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-23 14:27
 **/
@Slf4j
@Service
@Transactional
public class AlipayServiceImpl extends ChannelsAbstractAdapter implements AlipayService {

    @Autowired
    private ChannelsFeign channelsFeign;
    @Autowired
    private OrderRefundMapper orderRefundMapper;
    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private ReconciliationMapper reconciliationMapper;
    @Autowired
    private ClearingService clearingService;
    @Autowired
    private RabbitMQSender rabbitMQSender;
    @Autowired
    private OrdersMapper ordersMapper;
    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     **/
    @Override
    public BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        AliPayRefundDTO aliPayRefundDTO = new AliPayRefundDTO(orderRefund, channel);
        log.info("=================【AliPay退款】=================【请求Channels服务AliPay退款退款】请求参数 aliPayRefundDTO: {} ", JSON.toJSONString(aliPayRefundDTO));
        BaseResponse response = channelsFeign.alipayRefund(aliPayRefundDTO);
        log.info("=================【AliPay退款】=================【Channels服务响应】请求参数 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals("200")) {
            //请求成功
            Map<String, String> map = (Map<String, String>) response.getData();
            if (response.getMsg().equals("success")) {
                //退款成功
                log.info("=====================【AliPay退款】==================== 退款成功 : {} ", JSON.toJSON(orderRefund));
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, map.get("alipay_trans_id"), null);
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
            } else {
                log.info("=====================【AliPay退款】==================== 【退款失败】 : {} ", JSON.toJSON(orderRefund));
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                String type = orderRefund.getRemark4().equals(TradeConstant.RF )? TradeConstant.AA : TradeConstant.RA;
                Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, TradeConstant.REFUND_FAIL_RECONCILIATION);
                reconciliationMapper.insert(reconciliation);
                FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                log.info("=========================【AliPay退款】======================= 【调账 {}】， fundChangeDTO:【{}】",type, JSON.toJSONString(fundChangeDTO));
                BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                    //调账成功
                    log.info("=================【AliPay退款】=================【调账成功】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                    reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundFail(orderRefund);
                } else {
                    //调账失败
                    log.info("=================【AliPay退款】=================【调账失败】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    RabbitMassage rabbitMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                    log.info("=================【NextPos退款】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                    rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
                }
            }
        } else {
            log.info("=====================【AliPay退款】==================== 【请求失败】 : {} ", JSON.toJSON(orderRefund));
            baseResponse.setMsg(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【AliPay退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.TK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));

        }
        return baseResponse;
    }


    /**
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate    撤销
     * @return
     **/
    @Override
    public BaseResponse cancel(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
        if (rabbitMassage == null) {
            rabbitMassage = rabbitOrderMsg;
        }
        BaseResponse response = new BaseResponse();
        AliPayQueryDTO aliPayQueryDTO = new AliPayQueryDTO(orderRefund.getOrderId(), channel);
        log.info("=================【AliPay撤销】=================【请求Channels服务WeChant查询】aliPayQueryDTO : {}", JSON.toJSONString(aliPayQueryDTO));
        BaseResponse baseResponse = channelsFeign.alipayQuery(aliPayQueryDTO);
        log.info("=================【AliPay撤销】=================【Channels服务响应】baseResponse : {}", JSON.toJSONString(baseResponse));
        if (baseResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            Map<String, String> map = (Map<String, String>) baseResponse.getData();
            if (map.get("is_success").equals("T") && map.get("result_code").equals("SUCCESS")) {
                //查询成功，查看交易状态
                if (map.get("alipay_trans_status").equals("TRADE_SUCCESS")) {
                    //交易成功
                    if (ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_SUCCESS,null, new Date()) == 1) {
                        //更新成功
                        this.cancelPaying(channel, orderRefund,null);
                    } else {
                        //更新失败后去查询订单信息
                        log.info("=================【AliPay撤销】================= 【更新失败】orderId : {}", orderRefund.getOrderId());
                        rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
                    }
                }else{
                    //交易失败
                    log.info("=================【AliPay撤销】================= 【交易失败】orderId : {}", orderRefund.getOrderId());
                    ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_FAILD, null, new Date());
                }
            }else if ((map.get("is_success").equals("F") && !map.get("error").equals("SYSTEM_ERROR"))
                    || (map.get("is_success").equals("T") && map.get("result_code").equals("FAIL") && !map.get("detail_error_code").equals("SYSTEM_ERROR"))) {
                //明确查询失败
                //请求失败
                log.info("=================【AliPay撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
                rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
            } else {
                //请求失败
                log.info("=================【AliPay撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
                rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
            }
        }else{
            //请求失败
            log.info("=================【AliPay撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
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
    public BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        AliPayRefundDTO aliPayRefundDTO = new AliPayRefundDTO(orderRefund, channel);
        log.info("=================【AliPay撤销 cancelPaying】=================【请求Channels服务AliPay退款】请求参数 aliPayRefundDTO: {} ", JSON.toJSONString(aliPayRefundDTO));
        BaseResponse response = channelsFeign.alipayRefund(aliPayRefundDTO);
        log.info("=================【AliPay撤销 cancelPaying】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals("200")) {
            //请求成功
            if (response.getMsg().equals("success")) {
                //撤销成功
                log.info("=================【AliPay撤销 cancelPaying】=================【撤销成功】orderId : {}",orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(),orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
            } else {
                //撤销失败
                log.info("=================【AliPay撤销 cancelPaying】=================【撤销失败】orderId : {}",orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            }
        } else {//请求失败
            //请求失败
            log.info("=================【AliPay撤销 cancelPaying】=================【请求失败】orderId : {}",orderRefund.getOrderId());
            RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            if (rabbitMassage == null) {
                rabbitMassage = rabbitOrderMsg;
            }
            log.info("=================【AliPay撤销 cancelPaying】=================【上报通道】rabbitMassage: {} ", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.CX_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }
}
