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
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.doku.DokuService;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.dto.DokuBrowserCallbackDTO;
import com.asianwallets.trade.dto.DokuServerCallbackDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private AD3ParamsConfig ad3ParamsConfig;

    /**
     * Doku线上网银
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        DOKURequestDTO dokuRequestDTO = new DOKURequestDTO(orders, channel);
        DOKUReqDTO dokuReqDTO = new DOKUReqDTO();
        dokuReqDTO.setDokuRequestDTO(dokuRequestDTO);
        dokuReqDTO.setKey(channel.getMd5KeyStr());
        log.info("============【DOKU收单】============【请求参数】 dokuReqDTO: {}", JSON.toJSONString(dokuReqDTO));
        BaseResponse response = channelsFeign.dokuPay(dokuReqDTO);
        log.info("============【DOKU收单】============【响应参数】 baseResponse: {}", JSON.toJSONString(response));
        if (response == null || !TradeConstant.HTTP_SUCCESS.equals(response.getCode())) {
            log.info("============【DOKU收单】============【响应参数异常】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(response.getData());
        return baseResponse;
    }

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
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);
            } else {
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
            } else {
                //其他状态
                response.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【DOKU撤销】================= 【其他状态】orderId : {}", orderRefund.getOrderId());
            }
        } else {
            //请求失败
            //请求失败
            response.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【DOKU撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
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
            } else {
                //撤销失败
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【DOKU撤销 cancelPaying】=================【撤销失败】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            }

        } else {
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

    /***
     * doku服务器回调
     * @param dokuServerCallbackDTO doku服务器回调实体
     * @return
     */
    @Override
    public void dokuServerCallback(DokuServerCallbackDTO dokuServerCallbackDTO) {
        //校验参数
        if (StringUtils.isEmpty(dokuServerCallbackDTO.getRESULTMSG())) {
            log.info("==============【DOKU服务器回调接口】==============【订单状态为空】");
            return;
        }
        if (StringUtils.isEmpty(dokuServerCallbackDTO.getWORDS())) {
            log.info("==============【DOKU服务器回调接口】==============【签名为空】");
            return;
        }
        if (StringUtils.isEmpty(dokuServerCallbackDTO.getTRANSIDMERCHANT())) {
            log.info("==============【DOKU服务器回调接口】==============【订单流水号为空】");
            return;
        }
        //校验原订单
        Orders orders = ordersMapper.selectByPrimaryKey(dokuServerCallbackDTO.getTRANSIDMERCHANT());
        if (orders == null) {
            log.info("==============【DOKU服务器回调接口】==============【原订单不存在】");
            return;
        }
        //查询通道信息
        Channel channel = commonRedisDataService.getChannelByChannelCode(orders.getChannelCode());
        String sign = createSign(dokuServerCallbackDTO.getAMOUNT() + channel.getChannelMerchantId() + channel.getMd5KeyStr()
                + dokuServerCallbackDTO.getTRANSIDMERCHANT() + dokuServerCallbackDTO.getRESULTMSG() + dokuServerCallbackDTO.getVERIFYSTATUS());
        //验签
        if (!dokuServerCallbackDTO.getWORDS().equals(sign)) {
            log.info("==============【DOKU服务器回调接口】==============【签名不匹配】");
            return;
        }
        //校验订单信息
        if (new BigDecimal(dokuServerCallbackDTO.getAMOUNT()).compareTo(orders.getTradeAmount()) != 0) {
            log.info("==============【DOKU服务器回调接口】==============【订单信息不匹配】");
            return;
        }
        //通道回调时间
        orders.setChannelCallbackTime(new Date());
        //通道流水号
        orders.setChannelNumber(dokuServerCallbackDTO.getAPPROVALCODE());
        //修改时间
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if ("SUCCESS".equalsIgnoreCase(dokuServerCallbackDTO.getRESPONSECODE())) {
            log.info("=================【DOKU服务器回调接口】=================【订单已支付成功】 orderId:{}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), dokuServerCallbackDTO.getAPPROVALCODE(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【DOKU服务器回调接口】=================【更新通道订单异常】", e);
            }
            //修改订单状态
            int i = ordersMapper.updateByExampleSelective(orders, example);
            if (i > 0) {
                log.info("=================【DOKU服务器回调接口】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【DOKU服务器回调接口】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    //上报清结算资金变动接口
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                    //请求成功
                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                        //业务处理失败
                        log.info("=================【DOKU服务器回调接口】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    } else {
                        log.info("=================【DOKU服务器回调接口】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【DOKU服务器回调接口】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【DOKU服务器回调接口】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("=================【DOKU服务器回调接口】=================【订单已支付失败】 orderId:{}", orders.getId());
            //支付失败
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            //上游返回的错误code
            orders.setRemark5(dokuServerCallbackDTO.getRESULTMSG());
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), dokuServerCallbackDTO.getAPPROVALCODE(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.info("=================【DOKU服务器回调接口】=================【更新通道订单异常】", e);
            }
            //计算支付失败时通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【DOKU服务器回调接口】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【DOKU服务器回调接口】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }
        //商户服务器回调地址不为空,回调商户服务器
        try {
            if (!StringUtils.isEmpty(orders.getServerUrl())) {
                commonBusinessService.replyReturnUrl(orders);
            }
        } catch (Exception e) {
            log.error("=================【DOKU服务器回调接口】=================【回调商户服务器异常】", e);
        }
    }

    /***
     * doku浏览器回调
     * @param dokuBrowserCallbackDTO doku服务器回调实体
     * @return
     */
    @Override
    public void dokuBrowserCallback(DokuBrowserCallbackDTO dokuBrowserCallbackDTO, HttpServletResponse response) {
        //校验参数
        if (StringUtils.isEmpty(dokuBrowserCallbackDTO.getTRANSIDMERCHANT())) {
            log.info("==============【DOKU浏览器回调接口】==============【订单流水号为空】");
            return;
        }
        //查询原订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(dokuBrowserCallbackDTO.getTRANSIDMERCHANT());
        if (orders == null) {
            log.info("==============【DOKU浏览器回调接口】==============【回调订单信息不存在】");
            return;
        }
        if (orders.getTradeStatus().equals(TradeConstant.ORDER_PAY_SUCCESS)) {
            //支付成功
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("==============【DOKU浏览器回调接口】==============【开始回调商户】");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付成功页面
                    response.sendRedirect(ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_SUCCESS);
                } catch (IOException e) {
                    log.error("==============【DOKU浏览器回调接口】==============【调用AW支付成功页面失败】", e);
                }
            }
        } else {
            //其他情况
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("==============【DOKU浏览器回调接口】==============【开始回调商户】");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付中页面
                    response.sendRedirect(ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_PROCESSING);
                } catch (IOException e) {
                    log.error("==============【DOKU浏览器回调接口】==============【调用AW支付中页面失败】", e);
                }
            }
        }
    }

    /**
     * 生成回调签名
     *
     * @param clearText 加密字符串
     * @return
     */
    private String createSign(String clearText) {
        log.error("===============【DOKU服务器回调接口】===============【签名前的明文】 clearText: {}", clearText);
        byte[] hashValue = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearText.getBytes(StandardCharsets.UTF_8));
            hashValue = messageDigest.digest();
        } catch (Exception e) {
            log.info("===============【DOKU服务器回调接口】===============【加签异常】", e);
        }
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        for (byte b : hashValue) {
            temp = Integer.toHexString(b & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
