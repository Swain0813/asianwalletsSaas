package com.asianwallets.trade.channels.alipay.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.alipay.*;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.AlipayCore;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.alipay.AlipayService;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.AplipayBrowserCallbackDTO;
import com.asianwallets.trade.dto.AplipayServerCallbackDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.CommonService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-23 14:27
 **/
@Slf4j
@Service
@Transactional
@HandlerType(TradeConstant.ALIPAY)
public class AlipayServiceImpl extends ChannelsAbstractAdapter implements AlipayService {

    @Autowired
    @Qualifier(value = "ad3ParamsConfig")
    private AD3ParamsConfig ad3ParamsConfig;

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private CommonService commonService;

    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        AliPayWebDTO aliPayWebDTO = new AliPayWebDTO(orders, channel,ad3ParamsConfig.getChannelCallbackUrl().concat("/onlineCallback/alipayBrowserCallback"), ad3ParamsConfig.getChannelCallbackUrl().concat("/onlinecallback/alipayServerCallback"));
        log.info("-----------------aliPayWebsite 支付宝线上下单 onlinePay-----------------请求实体 aliPayCSBDTO:{}", JSON.toJSONString(aliPayWebDTO));
        BaseResponse channelResponse = channelsFeign.aliPayWebsite(aliPayWebDTO);
        log.info("------------------aliPayWebsite 支付宝线上下单 onlinePay-----------------返回的参数 channelResponse:{}", JSON.toJSONString(channelResponse));
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(channelResponse.getData());
        return baseResponse;
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
        AliPayRefundDTO aliPayRefundDTO = new AliPayRefundDTO(orderRefund, channel);
        log.info("=================【AliPay退款】=================【请求Channels服务AliPay退款退款】请求参数 aliPayRefundDTO: {} ", JSON.toJSONString(aliPayRefundDTO));
        BaseResponse response = channelsFeign.alipayRefund(aliPayRefundDTO);
        log.info("=================【AliPay退款】=================【Channels服务响应】请求参数 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals("200")) {
            //请求成功
            Map<String, String> map = (Map<String, String>) response.getData();
            if (response.getMsg().equals("success")) {
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                //退款成功
                log.info("=====================【AliPay退款】==================== 退款成功 : {} ", JSON.toJSON(orderRefund));
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, map.get("alipay_trans_id"), null);
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);
            } else {
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=====================【AliPay退款】==================== 【退款失败】 : {} ", JSON.toJSON(orderRefund));
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                //退款失败调用清结算
                commonService.orderRefundFailFundChange(orderRefund,channel);
            }
        } else {
            log.info("=====================【AliPay退款】==================== 【请求失败】 : {} ", JSON.toJSON(orderRefund));
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【AliPay退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.TK_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));

        }
        return baseResponse;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate 撤销
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
                    if (ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_SUCCESS, null, new Date()) == 1) {
                        //更新成功
                        response = this.cancelPaying(channel, orderRefund, null);
                    } else {
                        //更新失败后去查询订单信息
                        response.setCode(EResultEnum.REFUNDING.getCode());
                        log.info("=================【AliPay撤销】================= 【更新失败】orderId : {}", orderRefund.getOrderId());
                        rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
                    }
                } else {
                    //交易失败
                    response.setCode(EResultEnum.REFUND_FAIL.getCode());
                    log.info("=================【AliPay撤销】================= 【交易失败】orderId : {}", orderRefund.getOrderId());
                    ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_FAILD, null, new Date());
                }
            } else if ((map.get("is_success").equals("F") && !map.get("error").equals("SYSTEM_ERROR"))
                    || (map.get("is_success").equals("T") && map.get("result_code").equals("FAIL") && !map.get("detail_error_code").equals("SYSTEM_ERROR"))) {
                //明确查询失败
                //请求失败
                response.setCode(EResultEnum.REFUNDING.getCode());
                log.info("=================【AliPay撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
                rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
            } else {
                //请求失败
                response.setCode(EResultEnum.REFUNDING.getCode());
                log.info("=================【AliPay撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
                rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
            }
        } else {
            //请求失败
            response.setCode(EResultEnum.REFUNDING.getCode());
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
                response.setCode(EResultEnum.REFUNDING.getCode());
                log.info("=================【AliPay撤销 cancelPaying】=================【撤销成功】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
            } else {
                //撤销失败
                response.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【AliPay撤销 cancelPaying】=================【撤销失败】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            }
        } else {//请求失败
            //请求失败
            response.setCode(EResultEnum.REFUNDING.getCode());
            log.info("=================【AliPay撤销 cancelPaying】=================【请求失败】orderId : {}", orderRefund.getOrderId());
            RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            if (rabbitMassage == null) {
                rabbitMassage = rabbitOrderMsg;
            }
            log.info("=================【AliPay撤销 cancelPaying】=================【上报通道】rabbitMassage: {} ", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.CX_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }

    /**
     * 支付宝线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        AliPayCSBDTO aliPayCSBDTO = new AliPayCSBDTO(orders, channel, ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/aliPayCsbServerCallback"));
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【Alipay线下CSB接口请求参数】 aliPayCSBDTO: {}", JSON.toJSONString(aliPayCSBDTO));
        BaseResponse channelResponse = channelsFeign.aliPayCSB(aliPayCSBDTO);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【Alipay线下CSB接口响应参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下CSB动态扫码】==================【Channels服务响应结果不正确】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(channelResponse.getData());
        return baseResponse;
    }

    /**
     * 支付宝线下BSC
     *
     * @param orders   订单
     * @param channel  通道
     * @param authCode 付款码
     * @return
     */
    @Override
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode) {
        AliPayOfflineBSCDTO aliPayOfflineBSCDTO = new AliPayOfflineBSCDTO(orders, channel, authCode);
        log.info("==================【线下BSC动态扫码】==================【调用Channels服务】【Alipay线下BSC接口请求参数】 aliPayOfflineBSCDTO: {}", JSON.toJSONString(aliPayOfflineBSCDTO));
        BaseResponse channelResponse = channelsFeign.aliPayOfflineBSC(aliPayOfflineBSCDTO);
        log.info("==================【线下BSC动态扫码】==================【调用Channels服务】【Alipay线下BSC接口响应参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
        //支付失败时
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下BSC动态扫码】==================【Channels服务响应结果不正确】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        JSONObject json = JSONObject.fromObject(channelResponse.getData());
        orders.setUpdateTime(new Date());
        orders.setChannelNumber(json.getString("alipay_trans_id"));
        orders.setChannelCallbackTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orders.getId());
        criteria.andEqualTo("tradeStatus", "2");
        if (TradeConstant.HTTP_SUCCESS_MSG.equals(channelResponse.getMsg())) {
            log.info("==================【线下BSC动态扫码】==================【订单已支付成功】 ordersId: {}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【线下BSC动态扫码】=================【更新通道订单异常】", e);
            }
            //更新订单信息
            if (ordersMapper.updateByPrimaryKeySelective(orders) == 1) {
                log.info("=======——————==========【线下BSC动态扫码】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("======——————===========【线下BSC动态扫码】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    commonService.fundChangePlaceOrderSuccess(orders);
                } catch (Exception e) {
                    log.error("=================【线下BSC动态扫码】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【线下BSC动态扫码】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else if (TradeConstant.HTTP_FAIL_MSG.equals(channelResponse.getMsg())) {
            log.info("=================【线下BSC动态扫码】=================【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark5(channelResponse.getMsg());
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), orders.getChannelNumber(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【线下BSC动态扫码】=================【更新通道订单异常】", e);
            }
            //计算支付失败时通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【线下BSC动态扫码】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【线下BSC动态扫码】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        return null;
    }

    /**
     * 支付宝CSB回调
     *
     * @param request
     * @param response
     */
    @Override
    public void aliPayCsbServerCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> param = new HashMap<>();
        //商户订单号
        String out_trade_no = null;
        //支付宝交易号
        String trade_no = null;
        //获取支付宝POST过来反馈信息
        Map<String, String[]> requestParams = request.getParameterMap();
        try {
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                log.info("=================【aliPay支付CSB扫码回调】================= name:{} | valueStr:{}", name, valueStr);
                param.put(name, valueStr);
            }
            out_trade_no = param.get("out_trade_no");
            trade_no = param.get("trade_no");
            //交易状态
            String trade_status = param.get("trade_status");
            //签名方式
            String sign_type = param.get("sign_type");
            //签名
            String sign = param.get("sign");
            if (out_trade_no != null && !out_trade_no.equals("") && trade_no != null && !trade_no.equals("") && trade_status != null && !trade_status.equals("")
                    && sign_type != null && !sign_type.equals("") && sign != null && !sign.equals("")) {
                //查询原订单信息
                Orders orders = ordersMapper.selectByPrimaryKey(out_trade_no);
                //查询通道md5key
                ChannelsOrder channelsOrder = channelsOrderMapper.selectByPrimaryKey(out_trade_no);
                if (null != orders) {
                    orders.setChannelCallbackTime(new Date());//通道回调时间
                    orders.setChannelNumber(trade_no);//通道流水号
                    orders.setUpdateTime(new Date());//修改时间
                    Example example = new Example(Orders.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("tradeStatus", "2");
                    criteria.andEqualTo("id", orders.getId());
                    if (verifySign(param, channelsOrder.getMd5KeyStr())) {
                        //状态为交易完成
                        if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
                            log.info("==================【aliPay支付CSB扫码服务器回调】==================【订单已支付成功】 orderId: {}", orders.getId());
                            //未发货
                            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
                            //未签收
                            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
                            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
                            try {
                                //更改channelsOrders状态
                                channelsOrderMapper.updateStatusById(orders.getId(), out_trade_no, TradeConstant.TRADE_SUCCESS);
                            } catch (Exception e) {
                                log.info("==================【aliPay支付CSB扫码服务器回调】==================【更新通道订单异常】 orderId: {}", orders.getId());
                            }
                            if (ordersMapper.updateByExampleSelective(orders, example) > 0) {
                                log.info("=================【aliPay支付CSB扫码回调】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                                log.info("=================【aliPay支付CSB扫码服务器回调】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                                //计算支付成功时的通道网关手续费
                                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                                //TODO 添加日交易限额与日交易笔数
                                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                                //支付成功后向用户发送邮件
                                commonBusinessService.sendEmail(orders);
                                try {
                                    //账户信息不存在的场合创建对应的账户信息
                                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                                        log.info("=================【aliPay支付CSB扫码服务器回调】=================【上报清结算前线下下单创建账户信息】");
                                        commonBusinessService.createAccount(orders);
                                    }
                                    //分润
                                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                                    }
                                    //更新成功,上报清结算
                                    commonService.fundChangePlaceOrderSuccess(orders);
                                } catch (Exception e) {
                                    log.error("=================【aliPay支付CSB扫码服务器回调】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                                }
                            } else {
                                log.info("=================【aliPay支付CSB扫码服务器回调】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
                            }
                        } else {
                            log.info("==================【aliPay支付CSB扫码服务器回调】==================【订单已支付失败】 orderId: {}", orders.getId());
                            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                            orders.setRemark5(trade_status);
                            //更改channelsOrders状态
                            try {
                                channelsOrderMapper.updateStatusById(orders.getId(), trade_no, TradeConstant.TRADE_FALID);
                            } catch (Exception e) {
                                log.info("==================【aliPay支付CSB扫码服务器回调】==================【更新通道订单异常】 orderId: {}", orders.getId());
                            }
                            //计算支付失败时通道网关手续费
                            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
                            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                                log.info("=================【aliPay支付CSB扫码回调】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
                            } else {
                                log.info("=================【aliPay支付CSB扫码回调】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
                            }
                        }
                        response.getWriter().write("success");
                    } else {
                        log.info("=================【aliPay支付CSB扫码回调】=================【返回验签失败】");
                    }
                } else {
                    log.info("=================【aliPay支付CSB扫码回调】=================【返回找不到交易,交易号】  out_trade_no: {}", out_trade_no);
                }
            } else {
                log.info("=================【aliPay支付CSB扫码回调】=================【支付宝返回的订单号为空】");
            }
        } catch (Exception e) {
            log.info("=================【aliPay支付CSB扫码回调】=================【接口异常】", e);
        }
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/6/24
     * @Descripate 支付宝CSB验签
     **/
    public boolean verifySign(Map<String, String> params, String md5Key) {
        String sign = "";
        if (params.get("sign") != null) {
            sign = params.get("sign");
        }
        Map<String, String> sParaNew = AlipayCore.paraFilter(params);
        //获取待签名字符串
        String clearText = AlipayCore.createLinkString(sParaNew);
        //获得签名验证结果
        clearText = clearText + md5Key;
        log.info("=================【aliPay支付CSB扫码回调】=================【签名前的明文】 clearText: {}", clearText);
        String mySign = DigestUtils.md5Hex(clearText.getBytes(StandardCharsets.UTF_8));
        return mySign.equals(sign);
    }

    /**
     * apipay线上服务器回调
     * @param aplipayServerCallbackDTO
     * @param map
     */
    @Override
    public void aplipayServerCallback(AplipayServerCallbackDTO aplipayServerCallbackDTO,Map<String, String> map) {
        //校验参数
        if (StringUtils.isEmpty(aplipayServerCallbackDTO.getSign())) {
            log.info("==============【apipay线上服务器回调】==============【签名方式为空】");
            return;
        }
        if (StringUtils.isEmpty(aplipayServerCallbackDTO.getSign_type())) {
            log.info("==============【apipay线上服务器回调】==============【签名为空】");
            return;
        }
        if (StringUtils.isEmpty(aplipayServerCallbackDTO.getOut_trade_no())) {
            log.info("==============【apipay线上服务器回调】==============【商户订单号为空】");
            return;
        }
        if (StringUtils.isEmpty(aplipayServerCallbackDTO.getTrade_no())) {
            log.info("==============【apipay线上服务器回调】==============【支付宝交易号为空】");
            return;
        }
        if (StringUtils.isEmpty(aplipayServerCallbackDTO.getTrade_status())) {
            log.info("==============【apipay线上服务器回调】==============【交易状态为空】");
            return;
        }
        //校验原订单
        Orders orders = ordersMapper.selectByPrimaryKey(aplipayServerCallbackDTO.getOut_trade_no());
        if (orders == null) {
            log.info("==============【apipay线上服务器回调】==============【原订单不存在】");
            return;
        }
        //查询通道信息
        Channel channel = commonRedisDataService.getChannelByChannelCode(orders.getChannelCode());
        if(!verify(map,channel.getMd5KeyStr())){
            log.info("==============【apipay线上服务器回调】==============【签名不匹配】");
            return;
        }
        //通道回调时间
        orders.setChannelCallbackTime(new Date());
        //通道流水号
        orders.setChannelNumber(aplipayServerCallbackDTO.getTrade_no());
        //修改时间
        orders.setUpdateTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        //状态为交易完成
        if(aplipayServerCallbackDTO.getTrade_status().equals("TRADE_FINISHED")||aplipayServerCallbackDTO.getTrade_status().equals("TRADE_SUCCESS")){
            log.info("=================【apipay线上服务器回调】=================【订单已支付成功】 orderId:{}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus((TradeConstant.ORDER_PAY_SUCCESS));
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), aplipayServerCallbackDTO.getTrade_no(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【apipay线上服务器回调】=================【更新通道订单异常】", e);
            }

            //修改订单状态
            int i = ordersMapper.updateByExampleSelective(orders, example);
            if (i > 0) {
                log.info("=================【apipay线上服务器回调】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【apipay线上服务器回调】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    commonService.fundChangePlaceOrderSuccess(orders);
                } catch (Exception e) {
                    log.error("=================【apipay线上服务器回调】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【apipay线上服务器回调】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        }else {
            log.info("=================【apipay线上服务器回调】=================【订单已支付失败】 orderId:{}", orders.getId());
            //支付失败
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            //上游返回的错误code
            orders.setRemark5("fail");
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), aplipayServerCallbackDTO.getTrade_no(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.info("=================【apipay线上服务器回调】=================【更新通道订单异常】", e);
            }
            //计算支付失败时通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【apipay线上服务器回调】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【apipay线上服务器回调】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }

        //商户服务器回调地址不为空,回调商户服务器
        try {
            if (!StringUtils.isEmpty(orders.getServerUrl())) {
                commonBusinessService.replyReturnUrl(orders);
            }
        } catch (Exception e) {
            log.error("=================【apipay线上服务器回调】=================【回调商户服务器异常】", e);
        }
    }

    /**
     * 验证消息是否是支付宝发出的合法消息
     * @param params 通知返回来的参数数组
     * @return 验证结果
     */
    public static boolean verify(Map<String, String> params,String md5KeyStr) {
        //判断responsetTxt是否为true，isSign是否为true
        String responseTxt = "true";
        String sign = "";
        if(params.get("sign") != null) {sign = params.get("sign");}
        boolean isSign = getSignVeryfy(params, sign, md5KeyStr);
        if (isSign && responseTxt.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据反馈回来的信息，生成签名结果
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
     */
    private static boolean getSignVeryfy(Map<String, String> Params, String sign, String md5KeyStr) {
        //过滤空值、sign与sign_type参数
        Map<String, String> sParaNew = AlipayCore.paraFilter(Params);
        //获取待签名字符串
        String preSignStr = AlipayCore.createLinkString(sParaNew);
        //获得签名验证结果
        boolean isSign = false;
        isSign = verifyCheck(preSignStr, sign, md5KeyStr, "utf-8");
        return isSign;
    }

    /**
     * 签名字符串
     * @param text 需要签名的字符串
     * @param sign 签名结果
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    private static boolean verifyCheck(String text, String sign, String key, String input_charset) {
        text = text + key;
        String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
        if(mysign.equals(sign)) {
            return true;
        }
        else {
            return false;
        }
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (Exception e) {
            throw new RuntimeException("支付宝线上下单---MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    /**
     * 支付宝线上下单浏览器回调
     * @param aplipayBrowserCallbackDTO
     * @param response
     */
    @Override
    public void aplipayBrowserCallback(AplipayBrowserCallbackDTO aplipayBrowserCallbackDTO, HttpServletResponse response) {
        //校验参数
        if (StringUtils.isEmpty(aplipayBrowserCallbackDTO.getOut_trade_no())) {
            log.info("==============【支付宝线上下单浏览器回调接口】==============【订单流水号为空】");
            return;
        }
        //查询原订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(aplipayBrowserCallbackDTO.getOut_trade_no());
        if (orders == null) {
            log.info("==============【支付宝线上下单浏览器回调接口】==============【回调订单信息不存在】");
            return;
        }
        if (orders.getTradeStatus().equals(TradeConstant.ORDER_PAY_SUCCESS)) {
            //支付成功
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("==============【支付宝线上下单浏览器回调接口】==============【开始回调商户】");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付成功页面
                    response.sendRedirect(ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_SUCCESS);
                } catch (IOException e) {
                    log.error("==============【支付宝线上下单浏览器回调接口】==============【调用AW支付成功页面失败】", e);
                }
            }
        } else {
            //其他情况
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("==============【支付宝线上下单浏览器回调接口】==============【开始回调商户】");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付中页面
                    response.sendRedirect(ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_PROCESSING);
                } catch (IOException e) {
                    log.error("==============【支付宝线上下单浏览器回调接口】==============【调用AW支付中页面失败】", e);
                }
            }
        }
    }

    /**
     * 支付宝码牌
     *
     * @return
     */
    @Override
    public BaseResponse codeTrading(Orders orders, Channel channel) {
        return null;
    }


    /**
     * 支付宝码牌回调
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    public String aliPayCodePayCallback(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
