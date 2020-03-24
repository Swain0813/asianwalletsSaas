package com.asianwallets.trade.channels.wechat.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.wechat.WechaRefundDTO;
import com.asianwallets.common.dto.wechat.WechatBSCDTO;
import com.asianwallets.common.dto.wechat.WechatCSBDTO;
import com.asianwallets.common.dto.wechat.WechatQueryDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.SignTools;
import com.asianwallets.common.utils.XMLUtil;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.wechat.WechantService;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-23 14:46
 **/
@Slf4j
@Service
@Transactional
@HandlerType(TradeConstant.WECHAT)
public class WechantServiceImpl extends ChannelsAbstractAdapter implements WechantService {


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
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     **/
    @Override
    public BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        BaseResponse baseResponse = new BaseResponse();
        WechaRefundDTO wechaRefundDTO = new WechaRefundDTO(orderRefund, channel);
        log.info("=================【WeChant退款】=================【请求Channels服务WeChant退款退款】请求参数 wechaRefundDTO: {} ", JSON.toJSONString(wechaRefundDTO));
        BaseResponse response = channelsFeign.wechatRefund(wechaRefundDTO);
        log.info("=================【WeChant退款】=================【Channels服务响应】请求参数 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals("200")) {
            //请求成功
            Map<String, String> map = (Map<String, String>) response.getData();
            if (map != null && map.get("return_code") != null && map.get("return_code").equals("SUCCESS")
                    && map.get("result_code") != null && !map.get("result_code").equals("") && map.get("result_code").equals("SUCCESS")) {
                //退款成功
                baseResponse.setCode(EResultEnum.SUCCESS.getCode());
                log.info("=================【WeChant退款】=================【退款成功】 response: {} ", JSON.toJSONString(response));
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, map.get("refund_id"), null);
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
                //退还分润
                commonBusinessService.refundShareBinifit(orderRefund);
            } else {
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=================【WeChant退款】=================【退款失败】 response: {} ", JSON.toJSONString(response));
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                String type = orderRefund.getRemark4().equals(TradeConstant.RF) ? TradeConstant.AA : TradeConstant.RA;
                String reconciliationRemark = type.equals(TradeConstant.AA) ? TradeConstant.REFUND_FAIL_RECONCILIATION : TradeConstant.CANCEL_ORDER_REFUND_FAIL;
                Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, reconciliationRemark);
                reconciliationMapper.insert(reconciliation);
                FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                log.info("=========================【WeChant退款】======================= 【调账 {}】， fundChangeDTO:【{}】", type, JSON.toJSONString(fundChangeDTO));
                BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                    //调账成功
                    log.info("=================【WeChant退款】=================【调账失败】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                    reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundFail(orderRefund);
                } else {
                    //调账失败
                    log.info("=================【WeChant退款】=================【调账失败】 cFundChange: {} ", JSON.toJSONString(cFundChange));
                    RabbitMassage rabbitMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                    log.info("=================【WeChant退款】=================【调账失败 上报队列 RA_AA_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMsg));
                    rabbitMQSender.send(AD3MQConstant.RA_AA_FAIL_DL, JSON.toJSONString(rabbitMsg));
                }
            }
        } else {
            //请求失败
            baseResponse.setCode(EResultEnum.REFUNDING.getCode());
            if (rabbitMassage == null) {
                rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            }
            log.info("===============【WeChant退款】===============【请求失败 上报队列 TK_SB_FAIL_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
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
        WechatQueryDTO wechatQueryDTO = new WechatQueryDTO(orderRefund.getOrderId(), channel);
        log.info("=================【WeChant撤销】=================【请求Channels服务WeChant查询】wechatQueryDTO : {}", JSON.toJSONString(wechatQueryDTO));
        BaseResponse baseResponse = channelsFeign.wechatQuery(wechatQueryDTO);
        log.info("=================【WeChant撤销】=================【Channels服务响应】baseResponse : {}", JSON.toJSONString(baseResponse));
        if (baseResponse.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            Map<String, String> map = (Map<String, String>) baseResponse.getData();
            if (map.get("return_code").equals("SUCCESS")) {
                //请求成功
                if (map.get("result_code").equals("SUCCESS") && map.get("trade_state").equals("SUCCESS")) {
                    //更新订单状态
                    if (ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_SUCCESS, null, new Date()) == 1) {
                        //更新成功
                        this.cancelPaying(channel, orderRefund, null);
                    } else {//更新失败后去查询订单信息
                        log.info("=================【WeChant撤销】================= 【更新失败】orderId : {}", orderRefund.getOrderId());
                        rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
                    }
                } else {
                    //交易失败
                    log.info("=================【WeChant撤销】================= 【交易失败】orderId : {}", orderRefund.getOrderId());
                    ordersMapper.updateOrderByAd3Query(orderRefund.getOrderId(), TradeConstant.ORDER_PAY_FAILD, null, new Date());
                }
            } else {
                //请求失败
                log.info("=================【WeChant撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
                rabbitMQSender.send(AD3MQConstant.E_CX_GX_FAIL_DL, JSON.toJSONString(rabbitMassage));
            }
        } else {
            //请求失败
            log.info("=================【WeChant撤销】=================【查询订单失败】orderId : {}", orderRefund.getOrderId());
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
        WechaRefundDTO wechaRefundDTO = new WechaRefundDTO(orderRefund, channel);
        log.info("=================【WeChant撤销 cancelPaying】=================【请求Channels服务WeChant退款】请求参数 wechaRefundDTO: {} ", JSON.toJSONString(wechaRefundDTO));
        BaseResponse response = channelsFeign.wechatRefund(wechaRefundDTO);
        log.info("=================【WeChant撤销 cancelPaying】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals("200")) {
            //请求成功
            Map<String, String> map = (Map<String, String>) response.getData();
            if (map != null && map.get("return_code") != null && map.get("return_code").equals("SUCCESS")
                    && map.get("result_code") != null && !map.get("result_code").equals("") && map.get("result_code").equals("SUCCESS")) {
                //撤销成功
                log.info("=================【WeChant撤销 cancelPaying】=================【撤销成功】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_SUCCESS);
            } else {
                //撤销失败
                log.info("=================【WeChant撤销 cancelPaying】=================【撤销失败】orderId : {}", orderRefund.getOrderId());
                ordersMapper.updateOrderCancelStatus(orderRefund.getMerchantOrderId(), orderRefund.getOperatorId(), TradeConstant.ORDER_CANNEL_FALID);
            }
        } else {
            //请求失败
            log.info("=================【WeChant撤销 cancelPaying】=================【请求失败】orderId : {}", orderRefund.getOrderId());
            RabbitMassage rabbitOrderMsg = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            if (rabbitMassage == null) {
                rabbitMassage = rabbitOrderMsg;
            }
            log.info("=================【WeChant撤销 cancelPaying】=================【上报通道】rabbitMassage: {} ", JSON.toJSON(rabbitMassage));
            rabbitMQSender.send(AD3MQConstant.CX_SB_FAIL_DL, JSON.toJSONString(rabbitMassage));
        }
        return baseResponse;
    }

    /**
     * 微信线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        WechatCSBDTO wechatCSBDTO = new WechatCSBDTO(orders, channel, ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback").concat("/wechatCSBCallback"));
        log.info("==================【线下CSB动态扫码】==================调用Channels服务【Wechat-CSB接口】-请求实体  wechatCSBDTO: {}", JSON.toJSONString(wechatCSBDTO));
        BaseResponse channelResponse = channelsFeign.wechatOfflineCSB(wechatCSBDTO);
        log.info("==================【线下CSB动态扫码】==================调用Channels服务【Wechat-CSB接口】-响应结果  channelResponse:{}", channelResponse);
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下CSB动态扫码】==================【Channels服务响应结果不正确】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(channelResponse.getData());
        return baseResponse;
    }

    /**
     * 微信线下BSC
     *
     * @param orders   订单
     * @param channel  通道
     * @param authCode 付款码
     * @return
     */
    @Override
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode) {
        WechatBSCDTO wechatBSCDTO = new WechatBSCDTO(orders, channel, authCode);
        log.info("==================【线下BSC动态扫码】==================【调用Channels服务】【Wechat-BSC接口】-请求实体  WechatBSCDTO: {}", JSON.toJSONString(wechatBSCDTO));
        BaseResponse channelResponse = channelsFeign.wechatOfflineBSC(wechatBSCDTO);
        log.info("==================【线下BSC动态扫码】==================【调用Channels服务】【Wechat-BSC接口】-响应结果  channelResponse:{}", JSON.toJSONString(channelResponse));
        //支付失败时
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下BSC动态扫码】==================【Channels服务响应结果不正确】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        orders.setChannelCallbackTime(new Date());//通道回调时间
        orders.setChannelNumber(String.valueOf(channelResponse.getData()));//通道流水号
        orders.setUpdateTime(new Date());//修改时间
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if (channelResponse.getMsg().equals(TradeConstant.HTTP_SUCCESS_MSG)) {
            log.info("==================【线下BSC动态扫码】==================【订单已支付成功】 orderId:{}", orders.getId());
            //未发货
            orders.setDeliveryStatus(TradeConstant.UNSHIPPED);
            //未签收
            orders.setReceivedStatus(TradeConstant.NO_RECEIVED);
            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), String.valueOf(channelResponse.getData()), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("==================【线下BSC动态扫码】==================【通道订单更新异常】", e);
            }
            //修改订单状态
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【线下BSC动态扫码】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【线下BSC动态扫码】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!org.springframework.util.StringUtils.isEmpty(orders.getAgentCode()) || !org.springframework.util.StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                    //上报清结算资金变动接口
                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                        log.info("=================【线下BSC动态扫码】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【线下BSC动态扫码】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【线下BSC动态扫码】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else if (TradeConstant.HTTP_FAIL_MSG.equals(channelResponse.getMsg())) {
            log.info("==================【线下BSC动态扫码】==================订单已支付失败 orderId:{}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            //上游返回的错误code
            orders.setRemark5(channelResponse.getMsg());
            try {
                //更改channelsOrders状态
                channelsOrderMapper.updateStatusById(orders.getId(), String.valueOf(channelResponse.getData()), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("==================【线下BSC动态扫码】==================【通道订单更新异常】", e);
            }
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
     * 微信CSB回调
     *
     * @param request
     * @param response
     */
    @Override
    public void wechatCsbServerCallback(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            StringBuffer xmlStr = new StringBuffer();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                xmlStr.append(line);
            }
            String xmlCallback = String.valueOf(xmlStr);
            log.info("==============【微信CSB服务器回调】==============【xml格式回调参数】 xmlCallback:{}", xmlCallback);
            if (StringUtils.isEmpty(xmlCallback)) {
                log.info("==============【微信CSB服务器回调】==============回调参数为空");
                return;
            }
            //解析xml
            Map<String, String> map = XMLUtil.xml2MapForWeChat(xmlCallback);
            log.info("==============【微信CSB服务器回调】==============【解析后的xml格式回调参数】 map:{}", map);
            if (ArrayUtil.isEmpty(map)) {
                log.info("==============【微信CSB服务器回调】==============【解析后的map参数为空】");
                return;
            }
            //订单id
            String orderId = map.get("out_trade_no");
            //查询md5Key
            ChannelsOrder channelsOrder = channelsOrderMapper.selectByPrimaryKey(orderId);
            if (channelsOrder == null) {
                log.info("==============【微信CSB服务器回调】==============【通道订单信息为空】");
                return;
            }
            //查询原订单信息
            Orders orders = ordersMapper.selectByPrimaryKey(orderId);
            if (orders == null) {
                log.info("==============【微信CSB服务器回调】==============【原订单信息为空】");
                return;
            }
            if (StringUtils.isNotBlank(map.get("return_code")) && "SUCCESS".equals(map.get("return_code"))) {
                //校验签名
                String apikey = channelsOrder.getMd5KeyStr();
                String wxSign = map.get("sign");
                map.remove("sign");
                String signTemp = SignTools.getWXSignStr(map);
                String mySign = SignTools.getWXSign_MD5(signTemp, apikey);
                if (wxSign.equals(mySign)) {
                    String transactionId = map.get("transaction_id");
                    orders.setChannelNumber(transactionId);
                    orders.setChannelCallbackTime(new Date());
                    orders.setUpdateTime(new Date());
                    Example example = new Example(Orders.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("id", orders.getId());
                    criteria.andEqualTo("tradeStatus", "2");
                    if (StringUtils.isNotBlank(map.get("return_code")) && map.get("result_code").equals("SUCCESS")) {
                        log.info("==============【微信CSB服务器回调】==============【订单已支付成功】 orderId:{}", orderId);
                        orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
                        //更改channelsOrders状态
                        try {
                            channelsOrderMapper.updateStatusById(orderId, transactionId, TradeConstant.TRADE_SUCCESS);
                        } catch (Exception e) {
                            log.error("==============【微信CSB服务器回调】==============【更新通道订单异常】", e);
                        }
                        //修改订单状态
                        if (ordersMapper.updateByExampleSelective(orders, example) > 0) {
                            log.info("=================【微信CSB服务器回调】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                            //计算支付成功时的通道网关手续费
                            commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                            //TODO 添加日交易限额与日交易笔数
                            //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                            //支付成功后向用户发送邮件
                            commonBusinessService.sendEmail(orders);
                            try {
                                //账户信息不存在的场合创建对应的账户信息
                                if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                                    log.info("=================【微信CSB服务器回调】=================【上报清结算前线下下单创建账户信息】");
                                    commonBusinessService.createAccount(orders);
                                }
                                //分润
                                if (!org.springframework.util.StringUtils.isEmpty(orders.getAgentCode()) || !org.springframework.util.StringUtils.isEmpty(orders.getRemark8())) {
                                    rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                                }
                                FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                                //上报清结算资金变动接口
                                BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                                if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                                    log.info("=================【微信CSB服务器回调】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                                }
                            } catch (Exception e) {
                                log.error("=================【微信CSB服务器回调】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                                RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                                rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                            }
                        } else {
                            log.info("=================【微信CSB服务器回调】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
                        }
                        //系统业务逻辑处理完成后需要给微信返回消息表示接受成功
                        String resultMsg = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml> ";
                        PrintWriter out = response.getWriter();
                        out.write(resultMsg);
                        out.flush();
                        out.close();
                    } else {
                        log.info("==============【微信CSB服务器回调】==============【订单已支付失败】 orderId:{}", orderId);
                        orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
                        //上游返回的错误code
                        orders.setRemark5(map.get("result_code"));
                        //更改channelsOrders状态
                        try {
                            channelsOrderMapper.updateStatusById(orderId, transactionId, TradeConstant.TRADE_FALID);
                        } catch (Exception e) {
                            log.error("==============【微信CSB服务器回调】==============【更新通道订单异常】", e);
                        }
                        if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                            log.info("=================【微信CSB服务器回调】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
                        } else {
                            log.info("=================【微信CSB服务器回调】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
                        }
                    }
                } else {
                    log.info("==============【微信CSB服务器回调】==============【签名不匹配】");
                }
            } else {
                log.info("==============【微信CSB服务器回调】==============【返回return_code参数为失败】");
            }
        } catch (Exception e) {
            log.error("==============【微信CSB服务器回调】==============【接口异常】", e);
        }
    }
}
