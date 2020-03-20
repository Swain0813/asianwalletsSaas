package com.asianwallets.trade.channels.alipay.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.alipay.*;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.AlipayCore;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.alipay.AlipayService;
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
import java.math.BigDecimal;
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
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        BaseResponse baseResponse = new BaseResponse();
        AliPayWebDTO aliPayWebDTO = new AliPayWebDTO(orders, channel);
        log.info("-----------------aliPayWebsite aliPayw网站支付实体-----------------请求实体 aliPayCSBDTO:{}", JSON.toJSONString(aliPayWebDTO));
        int num = channelsOrderMapper.selectCountById(aliPayWebDTO.getOut_trade_no());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(aliPayWebDTO.getOut_trade_no());
        } else {
            co = new ChannelsOrder();
        }
        co.setMerchantOrderId(aliPayWebDTO.getInstitution_order_id());
        co.setTradeCurrency(aliPayWebDTO.getTrans_currency());
        co.setTradeAmount(new BigDecimal(aliPayWebDTO.getAmt()));
        co.setReqIp(aliPayWebDTO.getReqIp());
        //co.setDraweeName(eghlRequestDTO.getCustName());
        //co.setDraweeEmail(eghlRequestDTO.getCustEmail());
        co.setBrowserUrl(null);
        co.setServerUrl(aliPayWebDTO.getNotify_url());
        //co.setDraweePhone(eghlRequestDTO.getCustPhone());
        co.setTradeStatus(TradeConstant.TRADE_WAIT);
        //co.setIssuerId(enetsBankRequestDTO.getTxnReq().getMsg().getIssuingBank());
        co.setMd5KeyStr(aliPayWebDTO.getMd5KeyStr());
        co.setId(aliPayWebDTO.getOut_trade_no());
        co.setOrderType(AD3Constant.TRADE_ORDER);
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }

        String total_fee = null;
        String rmb_fee = null;
        if (aliPayWebDTO.getCurrency().equals("CNY")) {
            total_fee = "";
            rmb_fee = aliPayWebDTO.getAmt();
        } else {
            rmb_fee = "";
            total_fee = aliPayWebDTO.getAmt();
        }

        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", aliPayWebDTO.getService());//网站支付接口
        sParaTemp.put("partner", aliPayWebDTO.getPartner());//境外商户在支付宝的用户ID. 2088开头的16位数字
        sParaTemp.put("_input_charset", aliPayWebDTO.get_input_charset());//请求数据的编码集
        sParaTemp.put("notify_url", aliPayWebDTO.getNotify_url());//通知接收URL
        sParaTemp.put("return_url", aliPayWebDTO.getReturn_url());//交易付款成功之后，返回到商家网站的URL
        sParaTemp.put("out_trade_no", aliPayWebDTO.getOut_trade_no()); //境外商户交易号（确保在境外商户系统中唯一）
        sParaTemp.put("subject", aliPayWebDTO.getSubject());//商品标题
        if (aliPayWebDTO.getCurrency().equals("CNY")) {
            sParaTemp.put("rmb_fee", aliPayWebDTO.getAmt());
            sParaTemp.put("total_fee", "");
        } else {
            sParaTemp.put("rmb_fee", "");
            sParaTemp.put("total_fee", aliPayWebDTO.getAmt());
        }
        sParaTemp.put("body", aliPayWebDTO.getBody());//商品描述
        sParaTemp.put("currency", aliPayWebDTO.getCurrency()); //结算币种
        sParaTemp.put("timeout_rule", aliPayWebDTO.getTimeout_rule()); //有效时间
        //sParaTemp.put("product_code", product_code); //使用新接口需要加这个支付宝产品code
        sParaTemp.put("secondary_merchant_id", aliPayWebDTO.getSecondary_merchant_id()); //
        sParaTemp.put("secondary_merchant_name", aliPayWebDTO.getSecondary_merchant_name()); //
        sParaTemp.put("secondary_merchant_industry", aliPayWebDTO.getSecondary_merchant_industry()); //有效时间
        //sParaTemp.put("refer_url", refer_url); //二级商户网址
        log.info("-----------------aliPayWebsite 调用alipay的参数-----------------" + sParaTemp);
        Map<String, String> sPara = AlipayCore.buildRequestPara(sParaTemp, aliPayWebDTO.getMd5KeyStr());


        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<!DOCTYPE html>\n");
        stringBuffer.append("<html>\n");
        stringBuffer.append("<head>\n");
        stringBuffer.append("<title>ASIAN WALLET</title>\n");
        stringBuffer.append("</head>\n");
        stringBuffer.append("<body>\n");
        //stringBuffer.append("<form method=\"post\" name=\"SendForm\" action=\"" + channel.getPayUrl() + "\">\n");
        stringBuffer.append("<input type='hidden' name='service' value='" + aliPayWebDTO.getService() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='partner' value='" + aliPayWebDTO.getPartner() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='_input_charset' value='" + aliPayWebDTO.get_input_charset() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='notify_url' value='" + aliPayWebDTO.getNotify_url() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='return_url' value='" + aliPayWebDTO.getReturn_url() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='out_trade_no' value='" + aliPayWebDTO.getOut_trade_no() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='subject' value='" + aliPayWebDTO.getSubject() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='total_fee' value='" + total_fee + "'/>\n");
        stringBuffer.append("<input type='hidden' name='rmb_fee' value='" + rmb_fee + "'/>\n");
        stringBuffer.append("<input type='hidden' name='body' value='" + aliPayWebDTO.getBody() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='currency' value='" + aliPayWebDTO.getCurrency() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='timeout_rule' value='" + aliPayWebDTO.getTimeout_rule() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='secondary_merchant_id' value='" + aliPayWebDTO.getSecondary_merchant_id() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='secondary_merchant_name' value='" + aliPayWebDTO.getSecondary_merchant_name() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='secondary_merchant_industry' value='" + aliPayWebDTO.getSecondary_merchant_industry() + "'/>\n");
        stringBuffer.append("<input type='hidden' name='sign' value='" + sPara.get("sign") + "'/>\n");
        stringBuffer.append("<input type='hidden' name='sign_type' value='" + sPara.get("sign_type") + "'/>\n");
        stringBuffer.append("</form>\n");
        stringBuffer.append("</body>\n");
        stringBuffer.append("</html>");

        baseResponse.setData(stringBuffer.toString());
        log.info("-----------------eNets网银收单接口信息记录-----------------enetsBankRequestDTO:{}", stringBuffer.toString());
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
            } else {
                baseResponse.setCode(EResultEnum.REFUND_FAIL.getCode());
                log.info("=====================【AliPay退款】==================== 【退款失败】 : {} ", JSON.toJSON(orderRefund));
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                String type = orderRefund.getRemark4().equals(TradeConstant.RF) ? TradeConstant.AA : TradeConstant.RA;
                String reconciliationRemark = type.equals(TradeConstant.AA) ? TradeConstant.REFUND_FAIL_RECONCILIATION : TradeConstant.CANCEL_ORDER_REFUND_FAIL;
                Reconciliation reconciliation = commonBusinessService.createReconciliation(type, orderRefund, reconciliationRemark);
                reconciliationMapper.insert(reconciliation);
                FundChangeDTO fundChangeDTO = new FundChangeDTO(reconciliation);
                log.info("=========================【AliPay退款】======================= 【调账 {}】， fundChangeDTO:【{}】", type, JSON.toJSONString(fundChangeDTO));
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
        AliPayCSBDTO aliPayCSBDTO = new AliPayCSBDTO(orders, channel, ad3ParamsConfig.getChannelCallbackUrl().concat("/onlinecallback/aliPayCB_TPMQRCReturn"));
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
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
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
                                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                                    //上报清结算资金变动接口
                                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                                        log.info("=================【aliPay支付CSB扫码服务器回调】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                                    }
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
}
