package com.asianwallets.channels.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.AliPayService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.alipay.*;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class AliPayServiceImpl implements AliPayService {
    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    /**
     * aliPay查询
     *
     * @param aliPayQueryDTO 查询实体
     * @return
     */
    @Override
    public BaseResponse aliPayQuery(AliPayQueryDTO aliPayQueryDTO) {
        log.info("==================AliPay查询订单接口信息记录================== 请求参数 aliPayQueryDTO:{}", JSON.toJSONString(aliPayQueryDTO));
        //获取调用接口所需参数
        String queryUrl = aliPayQueryDTO.getChannel().getChannelSingleSelectUrl();//alipay查询接口请求地址
        //把请求参数打包成数组存入map
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("service", aliPayQueryDTO.getService());
        paramMap.put("partner", aliPayQueryDTO.getPartner());
        paramMap.put("_input_charset", aliPayQueryDTO.get_input_charset());
        paramMap.put("partner_trans_id", aliPayQueryDTO.getPartner_trans_id());
        //待请求参数数组
        Map<String, String> sPara = AlipayCore.buildRequestPara(paramMap, aliPayQueryDTO.getMd5KeyStr());
        //发送https请求到aliPay
        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
        HttpRequest aliPayRequest = new HttpRequest(HttpResultType.BYTES);
        //设置编码集
        aliPayRequest.setCharset(aliPayQueryDTO.get_input_charset());
        aliPayRequest.setParameters(AlipayCore.generatNameValuePair(sPara));
        aliPayRequest.setUrl(queryUrl + "_input_charset=" + aliPayQueryDTO.get_input_charset());
        BaseResponse baseResponse = new BaseResponse();
        //默认失败
        baseResponse.setCode(TradeConstant.HTTP_FAIL);
        baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        try {
            log.info("==================AliPay查询订单接口信息记录==================调用支付宝查询接口参数记录: aliPayRequest{}", aliPayRequest);
            HttpResponse aliPayResponse = httpProtocolHandler.execute(aliPayRequest, "", "");
            if (aliPayResponse == null || StringUtils.isEmpty(aliPayResponse.getStringResult())) {
                log.info("==================AliPay查询订单接口信息记录================== 返回结果为空");
                //网络异常或未知错误
                return baseResponse;
            }
            //解析xml响应结果为Map
            Map<String, String> resultMap = XMLUtil.parseAlipayResponseXML(aliPayResponse.getStringResult());
            log.info("==================AliPay查询订单接口信息记录==================Alipay查询接口响应报文: resultMap:{}", resultMap);
            //包装错误msg
            if (!StringUtils.isEmpty(resultMap.get("detail_error_code"))) {
                baseResponse.setMsg(resultMap.get("detail_error_code"));
                return baseResponse;
            }
            String aliPaySign = resultMap.get("sign");//获取支付宝返回的签名
            log.info("==================AliPay查询订单接口信息记录==================支付宝查询接口返回签名 aliPaySign : {}", aliPaySign);
            //除去数组中的空值和签名参数sign和sign_type
            Map<String, String> signPara = AlipayCore.paraFilterParam(resultMap);
            log.info("==================AliPay查询订单接口信息记录==================签名前的明文map signPara:{}", signPara);
            //生成签名结果
            String mySign = AlipayCore.buildRequestMysign(signPara, aliPayQueryDTO.getMd5KeyStr());
            log.info("==================AliPay查询订单接口信息记录==================签名后的密文 mySign : {}", mySign);
            //验签
            if (!mySign.equals(aliPaySign)) {
                log.info("==================AliPay查询订单接口信息记录==================签名不匹配");
                //验签失败
                return baseResponse;
            }
            //            map.put("alipay_trans_status", resultMap.get("alipay_trans_status")); //查询成功后的订单状态
            //            map.put("alipay_trans_id", resultMap.get("alipay_trans_id")); //alipay订单号
            //            map.put("partner_trans_id", resultMap.get("partner_trans_id")); //商户订单号
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(resultMap);
        } catch (Exception e) {
            log.error("==================AliPay查询订单接口信息记录==================发生异常", e);
        }
        return baseResponse;
    }

    /**
     * AliPay线下BSC收单方法
     *
     * @param aliPayOfflineBSCDTO aliPay线下BSC实体
     * @return
     */
    @Override
    public BaseResponse aliPayOfflineBSC(AliPayOfflineBSCDTO aliPayOfflineBSCDTO) {
        log.info("-----------------AliPay线下BSC收单接口信息记录-----------------请求参数记录 aliPayOfflineBSCDTO:{}", JSON.toJSONString(aliPayOfflineBSCDTO));
        int num = channelsOrderMapper.selectCountById(aliPayOfflineBSCDTO.getPartner_trans_id());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(aliPayOfflineBSCDTO.getPartner_trans_id());
        } else {
            co = new ChannelsOrder();
        }
        co.setInstitutionOrderId(aliPayOfflineBSCDTO.getInstitutionOrderId());
        co.setTradeCurrency(aliPayOfflineBSCDTO.getCurrency());
        co.setTradeAmount(new BigDecimal(aliPayOfflineBSCDTO.getTrans_amount()));
        co.setReqIp(aliPayOfflineBSCDTO.getReqIp());
        co.setTradeStatus(TradeConstant.TRADE_WAIT);
        co.setMd5KeyStr(aliPayOfflineBSCDTO.getMd5KeyStr());
        co.setId(aliPayOfflineBSCDTO.getPartner_trans_id());
        co.setOrderType(AD3Constant.TRADE_ORDER.toString());
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }

        BaseResponse baseResponse = new BaseResponse();
        //默认为错误
        baseResponse.setMsg("fail");
        baseResponse.setCode("302");
        //把请求参数打包成数组存入map
        Map<String, String> sParaTemp = new HashMap<>();
        sParaTemp.put("service", aliPayOfflineBSCDTO.getService());
        sParaTemp.put("partner", aliPayOfflineBSCDTO.getPartner());
        sParaTemp.put("_input_charset", aliPayOfflineBSCDTO.get_input_charset());
        sParaTemp.put("alipay_seller_id", aliPayOfflineBSCDTO.getAlipay_seller_id());
        sParaTemp.put("trans_name", aliPayOfflineBSCDTO.getTrans_name());
        sParaTemp.put("partner_trans_id", aliPayOfflineBSCDTO.getPartner_trans_id());
        sParaTemp.put("currency", aliPayOfflineBSCDTO.getCurrency());
        sParaTemp.put("trans_amount", aliPayOfflineBSCDTO.getTrans_amount());
        sParaTemp.put("buyer_identity_code", aliPayOfflineBSCDTO.getBuyer_identity_code());
        sParaTemp.put("identity_code_type", aliPayOfflineBSCDTO.getIdentity_code_type());
        sParaTemp.put("biz_product", aliPayOfflineBSCDTO.getBiz_product());
        sParaTemp.put("extend_info", aliPayOfflineBSCDTO.getExtend_info());
        try {
            //待请求参数数组
            Map<String, String> sPara = AlipayCore.buildRequestPara(sParaTemp, aliPayOfflineBSCDTO.getMd5KeyStr());
            //发送https请求到aliPay
            HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
            HttpRequest aliPayRequest = new HttpRequest(HttpResultType.BYTES);
            //设置编码集
            aliPayRequest.setCharset(aliPayOfflineBSCDTO.get_input_charset());
            aliPayRequest.setParameters(AlipayCore.generatNameValuePair(sPara));
            aliPayRequest.setUrl(aliPayOfflineBSCDTO.getChannel().getPayUrl() + "_input_charset=" + aliPayOfflineBSCDTO.get_input_charset());
            //调用支付宝BSC接口
            log.info("-----------------AliPay线下BSC收单接口信息记录-----------------开始调用支付宝BSC接口");
            HttpResponse aliPayResponse = httpProtocolHandler.execute(aliPayRequest, "", "");
            log.info("-----------------AliPay线下BSC收单接口信息记录-----------------结束调用支付宝BSC接口");
            if (aliPayResponse == null) {
                log.info("-----------------AliPay线下BSC收单接口信息记录-----------------alipay返回信息为null");
                Map<String, String> queryMap = aliPayQueryOrder(co.getId(), aliPayOfflineBSCDTO.getChannel().getChannelSingleSelectUrl());
                String isTradeSuccess = queryMap.get("queryStatus");//查询返回交易状态
                log.info("=====================AliPay线下BSC收单接口信息记录=======================未知情况下查询alipay返回状态:{}", isTradeSuccess);
                if ("SUCCESS".equals(isTradeSuccess)) {
                    String trade_status = queryMap.get("alipay_trans_status");//交易状态,查询成功才有
                    //查询成功，根据交易状态处理不同逻辑
                    if (trade_status.equals("TRADE_SUCCESS") || trade_status.equals("TRADE_CLOSED")) {
                        //支付成功时
                        log.info("====================AliPay线下BSC收单接口信息记录==================订单已支付成功");
                        baseResponse.setMsg("success");
                        baseResponse.setCode("200");
                        baseResponse.setData(queryMap);
                    } else if (trade_status.equals("WAIT_BUYER_PAY")) {
                        log.info("@@@@@@@@@@@@支付宝返回等待中@@@@@@@@@@@");
                        //等待付款，继续查询
                        for (int i = 0; i < 8; i++) {
                            Thread.sleep(3000); //线程等待3秒
                            queryMap = aliPayQueryOrder(co.getId(), aliPayOfflineBSCDTO.getChannel().getChannelSingleSelectUrl());
                            trade_status = queryMap.get("alipay_trans_status");//继续查询交易状态
                            if (trade_status.equals("TRADE_SUCCESS")) {
                                //支付成功时
                                log.info("====================AliPay线下BSC收单接口信息记录==================订单已支付成功");
                                baseResponse.setMsg("success");
                                baseResponse.setCode("200");
                                baseResponse.setData(queryMap);
                                return baseResponse;
                            }
                        }
                        //请求超时,返回失败
                        log.info("==============请求支付宝超时@@@===============支付失败");
                        baseResponse.setMsg("fail");
                        baseResponse.setCode("302");
                        return baseResponse;
                    } else {
                        log.info("======================AliPay线下BSC收单接口信息记录=========================未知状态,正常情况不存在====:{}", trade_status);
                    }
                } else {
                    log.info("====================AliPay线下BSC收单接口信息记录==================订单已失败 开始调用支付宝撤销接口");
                    //                    log.info("====================AliPay线下BSC收单接口信息记录==================订单已失败 开始调用支付宝撤销接口");
                    //                    //查询结果为失败的,调用取消订单接口
                    //                    String cancelStatus = aliPayCancelOrder(aliPayOfflineBSCDTO.getPartner_trans_id());
                    //                    if (cancelStatus.equals("UNKNOW")) {
                    //                        //撤销结果未知，继续调用撤销接口，3秒一次，最多5次
                    //                        for (int i = 0; i < 3; i++) {
                    //                            Thread.sleep(3000);//线程停止3秒再撤销
                    //                            cancelStatus = aliPayCancelOrder(aliPayOfflineBSCDTO.getPartner_trans_id());
                    //                            if (!cancelStatus.equals("UNKNOW")) {
                    //                                baseResponse.setMsg("fail");
                    //                                baseResponse.setCode("200");
                    //                                return baseResponse;
                    //                            }
                    //                        }
                    //                        if (cancelStatus.equals("UNKNOW")) {
                    //                            log.info("===================AliPay线下BSC收单接口信息记录=======================撤销4次支付宝返回未知=========请联系支付宝技术支持=====");
                    //                        }
                    //                    }
                    //                    log.info("===================AliPay线下BSC收单接口信息记录=======================订单已撤销");
                }
                return baseResponse;
            }
            //获取返回结果
            String result = aliPayResponse.getStringResult();
            if (StringUtils.isEmpty(result)) {
                log.info("-----------------AliPay线下BSC收单接口信息记录-----------------返回结果为空,重新调用查询接口查询订单真实状态");
                return baseResponse;
            }
            //国际aliPay返回
            log.info("-----------------AliPay线下BSC收单接口信息记录-----------------支付宝BSC接口返回结果 result:{}", result);
            //将支付宝返回的response标签中的xml字符串转换成map
            Map<String, String> resultMap = XMLUtil.parseAlipayResponseXML(result);
            log.info("-----------------AliPay线下BSC收单接口信息记录-----------------解析XML后的返回结果map resultMap:{}", resultMap);
            String is_success = resultMap.get("is_success");
            String result_code = resultMap.get("result_code");
            String aliPaySign = resultMap.get("sign");//获取支付宝返回的签名
            //除去数组中的空值和签名参数sign和is_success
            Map<String, String> signPara = AlipayCore.paraFilterParam(resultMap);
            log.info("-----------------AliPay线下BSC收单接口信息记录-----------------签名前的明文 signPara:{}", signPara);
            //生成签名结果
            String mySign = AlipayCore.buildRequestMysign(signPara, aliPayOfflineBSCDTO.getMd5KeyStr());
            log.info("-----------------AliPay线下BSC收单接口信息记录-----------------签名后的密文 mySign:{}", mySign);
            //验签
            if (!mySign.equals(aliPaySign)) {
                log.info("==================AliPay线下BSC收单接口信息记录==================签名不匹配,支付失败");
                return baseResponse;
            }
            //判断支付结果
            if (is_success.equals("T") && result_code.equals("SUCCESS")) {
                //支付成功时
                log.info("==================AliPay线下BSC收单接口信息记录==================订单已支付成功");
                baseResponse.setMsg("success");
                baseResponse.setCode("200");
                baseResponse.setData(resultMap);
            } else if ((is_success.equals("F") && !resultMap.get("error").equals("SYSTEM_ERROR")) ||
                    (is_success.equals("T") && result_code.equals("FAILED") && !resultMap.get("error").equals("SYSTEM_ERROR"))) {
                //支付失败时
                log.info("==================AliPay线下BSC收单接口信息记录==================订单已支付失败");
                baseResponse.setMsg("fail");
                baseResponse.setCode("302");
            } else {
                //未知情况需要调用查询接口重新查询
                Map<String, String> queryMap = aliPayQueryOrder(co.getId(), aliPayOfflineBSCDTO.getChannel().getChannelSingleSelectUrl());
                String isTradeSuccess = queryMap.get("queryStatus");//查询返回交易状态
                log.info("==================AliPay线下BSC收单接口信息记录=======================未知情况下查询alipay返回状态:{}", isTradeSuccess);
                if ("SUCCESS".equals(isTradeSuccess)) {
                    String trade_status = queryMap.get("alipay_trans_status");//交易状态,查询成功才有
                    //查询成功，根据交易状态处理不同逻辑
                    if (trade_status.equals("TRADE_SUCCESS") || trade_status.equals("TRADE_CLOSED")) {
                        //支付成功时
                        log.info("==================AliPay线下BSC收单接口信息记录==================订单已支付成功");
                        baseResponse.setMsg("success");
                        baseResponse.setCode("200");
                        baseResponse.setData(queryMap);
                    } else if (trade_status.equals("WAIT_BUYER_PAY")) {
                        //等待付款，继续查询
                        for (int i = 0; i < 8; i++) {
                            Thread.sleep(3000); //线程等待3秒
                            queryMap = aliPayQueryOrder(co.getId(), aliPayOfflineBSCDTO.getChannel().getChannelSingleSelectUrl());
                            trade_status = queryMap.get("alipay_trans_status");//继续查询交易状态
                            if (trade_status.equals("TRADE_SUCCESS")) {
                                //支付成功时
                                log.info("==================AliPay线下BSC收单接口信息记录==================订单已支付成功");
                                baseResponse.setMsg("success");
                                baseResponse.setCode("200");
                                baseResponse.setData(queryMap);
                                return baseResponse;
                            }
                        }
                    } else {
                        log.info("==================AliPay线下BSC收单接口信息记录=========================未知状态,正常情况不存在====:{}", trade_status);
                    }
                } else {
                    log.info("==============支付宝未知状态，需要重新查询3s一次，查询10次================");
                    //等待付款，继续查询
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(3000); //线程等待3秒
                        queryMap = aliPayQueryOrder(co.getId(), aliPayOfflineBSCDTO.getChannel().getChannelSingleSelectUrl());
                        String trade_status = queryMap.get("alipay_trans_status");//继续查询交易状态
                        if (trade_status.equals("TRADE_SUCCESS")) {
                            //支付成功时
                            log.info("====================AliPay线下BSC收单接口信息记录==================订单已支付成功");
                            baseResponse.setMsg("success");
                            baseResponse.setCode("200");
                            baseResponse.setData(queryMap);
                            return baseResponse;
                        }
                    }
                    //请求超时,返回失败
                    log.info("==============请求支付宝超时@@@===============");
                    baseResponse.setMsg("fail");
                    baseResponse.setCode("302");
                    return baseResponse;
                    //                    //查询结果为失败的,调用取消订单接口
                    //                    String cancelStatus = aliPayCancelOrder("");
                    //                    if (cancelStatus.equals("UNKNOW")) {
                    //                        //撤销结果未知，继续调用撤销接口，3秒一次，最多5次
                    //                        for (int i = 0; i < 3; i++) {
                    //                            Thread.sleep(3000); //线程停止3秒再撤销
                    //                            cancelStatus = aliPayCancelOrder("");
                    //                            if (!cancelStatus.equals("UNKNOW")) {
                    //                                //TODO
                    //                            }
                    //                        }
                    //                        if (cancelStatus.equals("UNKNOW")) {
                    //                            log.info("==================AliPay线下BSC收单接口信息记录=======================撤销4次支付宝返回未知=========请联系支付宝技术支持=====");
                    //                        }
                    //                    }
                }
            }
        } catch (Exception e) {
            log.error("==================AliPay线下BSC收单接口信息记录==================接口发生异常", e);
            baseResponse.setCode(EResultEnum.ERROR.getCode());
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        }
        return baseResponse;
    }


    /**
     * AliPay查询订单方法
     *
     * @param orderId                订单id
     * @param channelSingleSelectUrl
     * @return
     */
    @Override
    public Map<String, String> aliPayQueryOrder(String orderId, String channelSingleSelectUrl) {
        log.info("==================AliPay查询订单信息记录================== 请求参数 orderId:{}", orderId);
        //获取调用接口所需参数
        String queryUrl = channelSingleSelectUrl; //alipay查询接口请求地址
        String service = "alipay.acquire.overseas.query"; //支付接口名称 service_query=alipay.acquire.overseas.query
        String MD5Key = "foieh4q13dvezwvn0i03091rbgplkknl"; //用来加密的密钥
        String partner = "2088421920790891";//渠道商户号，支付宝分配
        String _input_charset = "UTF-8"; //编码格式
        //把请求参数打包成数组存入map
        Map<String, String> sParaTemp = new HashMap<>();
        sParaTemp.put("service", service);
        sParaTemp.put("partner", partner);
        sParaTemp.put("_input_charset", _input_charset);
        sParaTemp.put("partner_trans_id", orderId);
        //待请求参数数组
        Map<String, String> sPara = AlipayCore.buildRequestPara(sParaTemp, MD5Key);
        //发送https请求到aliPay
        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
        HttpRequest aliPayRequest = new HttpRequest(HttpResultType.BYTES);
        //设置编码集
        aliPayRequest.setCharset(_input_charset);
        aliPayRequest.setParameters(AlipayCore.generatNameValuePair(sPara));
        aliPayRequest.setUrl(queryUrl + "_input_charset=" + _input_charset);
        Map<String, String> map = new HashMap<>();
        try {
            log.info("==================AliPay查询订单信息记录==================调用支付宝查询接口参数记录: aliPayRequest{}", aliPayRequest);
            HttpResponse aliPayResponse = httpProtocolHandler.execute(aliPayRequest, "", "");
            if (aliPayResponse == null) {
                //网络异常或未知错误
                map.put("queryStatus", "UNKNOW");
                return map;
            }
            String result = aliPayResponse.getStringResult();
            if (StringUtils.isEmpty(result)) {
                //网络异常或未知错误
                return map;
            }
            //xml字符串转换成map
            Map<String, String> resultMap = XMLUtil.parseAlipayResponseXML(result);
            log.info("==================AliPay查询订单信息记录==================支付宝查询接口返回结果map resultMap:{}", resultMap);
            String aliPaySign = resultMap.get("sign");//获取支付宝返回的签名
            log.info("==================AliPay查询订单信息记录==================支付宝查询接口返回签名 aliPaySign:{}", aliPaySign);
            //除去数组中的空值和签名参数sign和sign_type
            Map<String, String> signPara = AlipayCore.paraFilterParam(resultMap);
            log.info("==================AliPay查询订单信息记录==================签名前的明文map signPara:{}", signPara);
            //生成签名结果
            String mySign = AlipayCore.buildRequestMysign(signPara, MD5Key);
            log.info("==================AliPay查询订单信息记录==================签名后的密文 mySign:{}", mySign);
            //签名与aliPay返回的签名一致则通过校验
            if (mySign.equals(aliPaySign)) {
                if (resultMap.get("is_success").equals("T") && resultMap.get("result_code").equals("SUCCESS")) {
                    log.info("==================AliPay查询订单信息记录==================订单状态为交易成功");
                    //查询成功，查看交易状态
                    map.put("queryStatus", "SUCCESS"); //查询成功
                    map.put("alipay_trans_status", resultMap.get("alipay_trans_status")); //查询成功后的订单状态
                    map.put("alipay_trans_id", resultMap.get("alipay_trans_id")); //alipay订单号
                    map.put("partner_trans_id", resultMap.get("partner_trans_id")); //商户订单号
                } else if ((resultMap.get("is_success").equals("F") && !resultMap.get("error").equals("SYSTEM_ERROR")) ||
                        (resultMap.get("is_success").equals("T") && resultMap.get("result_code").equals("FAIL") && !resultMap.get("detail_error_code").equals("SYSTEM_ERROR"))) {
                    //明确查询失败
                    map.put("queryStatus", "FAIL");
                } else {
                    //未知情况，继续调用查询接口 3秒调用一次，最多10次
                    map.put("queryStatus", "UNKNOW");
                }
            }
        } catch (Exception e) {
            log.error("==================AliPay查询订单信息记录==================发生异常", e);
            map.put("queryStatus", "UNKNOW");
        }
        return map;
    }


    /**
     * aliPay退款
     *
     * @param aliPayRefundDTO 退款实体
     * @return
     */
    @Override
    public BaseResponse aliPayRefund(AliPayRefundDTO aliPayRefundDTO) {

        ChannelsOrder co = new ChannelsOrder();
        co.setInstitutionOrderId(aliPayRefundDTO.getPartner_trans_id());
        co.setTradeCurrency(aliPayRefundDTO.getCurrency());
        co.setTradeAmount(new BigDecimal(aliPayRefundDTO.getRefund_amount()));
        //co.setReqIp(msg.get("ipAddress").toString());
        //co.setDraweeName(eghlRequestDTO.getCustName());
        //co.setDraweeEmail(eghlRequestDTO.getCustEmail());
        //co.setBrowserUrl(msg.get("b2sTxnEndURL").toString());
        //co.setServerUrl(msg.get("s2sTxnEndURL").toString());
        //co.setDraweePhone(eghlRequestDTO.getCustPhone());
        co.setTradeStatus(TradeConstant.TRADE_WAIT);
        //co.setIssuerId(enetsBankRequestDTO.getTxnReq().getMsg().getIssuingBank());
        //co.setMd5KeyStr(wechaRefundDTO.getApikey());
        co.setId(aliPayRefundDTO.getPartner_refund_id());
        co.setOrderType(AD3Constant.REFUND_ORDER.toString());
        co.setCreateTime(new Date());
        channelsOrderMapper.insert(co);


        log.info("==================AliPay退款接口信息记录==================参数记录 aliPayRefundDTO:{}", JSON.toJSONString(aliPayRefundDTO));
        //请求参数
        NameValuePair[] param = {
                new NameValuePair("service", aliPayRefundDTO.getService()),// 接口名称。
                new NameValuePair("_input_charset", aliPayRefundDTO.get_input_charset()),// 商户网站使用的编码格式，如utf-8、gbk、gb2312等。
                new NameValuePair("partner", aliPayRefundDTO.getPartner()),// 签约的支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。
                new NameValuePair("partner_refund_id", aliPayRefundDTO.getPartner_refund_id()),//支付宝合作商户网站唯一订单号（确保在商户系统中唯一）。
                new NameValuePair("partner_trans_id", aliPayRefundDTO.getPartner_trans_id()),//商家退款编号
                new NameValuePair("refund_amount", aliPayRefundDTO.getRefund_amount()),
                new NameValuePair("currency", aliPayRefundDTO.getCurrency()),
                new NameValuePair("sign_type", aliPayRefundDTO.getSign_type()),// 签名方式只支持DSA、RSA、MD5。
                new NameValuePair("sign", aliPayRefundDTO.getSign())};// 签名
        String url = aliPayRefundDTO.getChannel().getRefundUrl();//退款地址
        PostMethod post = new PostMethod(url);
        post.setRequestBody(param);
        HttpClient httpclient = new HttpClient();
        // 设置编码
        httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, aliPayRefundDTO.get_input_charset());
        BaseResponse baseResponse = new BaseResponse();
        try {
            int status = httpclient.executeMethod(post);
            String respStr = new String(post.getResponseBody(), aliPayRefundDTO.get_input_charset());
            log.info("==================AliPay退款接口信息记录==================支付宝退款接口返回: respStr{}", respStr);
            if (status == 200) {
                //注解方式xml转换为对象
                Map<String, String> map = XMLUtil.parseAlipayXML(respStr);
                if (null != map) {
                    String is_success = map.get("is_success");
                    if (!StringUtils.isEmpty(is_success) && StringUtils.equals("T", is_success)) {
                        String result_code = map.get("result_code");
                        if (!StringUtils.isEmpty(result_code) && StringUtils.equals("SUCCESS", result_code)) {
                            //退款受理成功
                            baseResponse.setCode("200");
                            baseResponse.setData(map);
                            baseResponse.setMsg("success");

                            channelsOrderMapper.updateStatusById(aliPayRefundDTO.getPartner_refund_id(), map.get("alipay_trans_id"), "2");
                        } else {
                            //退款受理失败
                            baseResponse.setCode("200");
                            baseResponse.setData(map);
                            baseResponse.setMsg("fail");
                            channelsOrderMapper.updateStatusById(aliPayRefundDTO.getPartner_refund_id(), map.get("alipay_trans_id"), "3");
                        }
                    } else {
                        //请求失败
                        baseResponse.setCode("200");
                        baseResponse.setData(map);
                        baseResponse.setMsg("fail");
                    }
                } else {
                    log.info("==================AliPay退款接口信息记录==================xml解析异常");
                    baseResponse.setCode("200");
                    baseResponse.setMsg("fail");
                }
            } else {
                log.info("==================AliPay退款接口信息记录==================状态码异常");
                baseResponse.setCode("400");
                baseResponse.setMsg("fail");
            }
        } catch (Exception e) {
            log.error("==================AliPay退款接口信息记录==================发生异常", e);
            baseResponse.setCode("302");
            baseResponse.setMsg("fail");
            //请求失败
        }
        return baseResponse;
    }

    /**
     * AliPay线下CSB收单方法
     *
     * @param aliPayCSBDTO aliPay线下CSB实体
     * @return
     */
    @Override
    public BaseResponse aliPayCSB(AliPayCSBDTO aliPayCSBDTO) {

        int num = channelsOrderMapper.selectCountById(aliPayCSBDTO.getOut_trade_no());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(aliPayCSBDTO.getOut_trade_no());
        } else {
            co = new ChannelsOrder();
        }
        co.setInstitutionOrderId(aliPayCSBDTO.getInstitution_order_id());
        co.setTradeCurrency(aliPayCSBDTO.getTrans_currency());
        co.setTradeAmount(new BigDecimal(aliPayCSBDTO.getTotal_fee()));
        co.setReqIp(aliPayCSBDTO.getReqIp());
        //co.setDraweeName(eghlRequestDTO.getCustName());
        //co.setDraweeEmail(eghlRequestDTO.getCustEmail());
        co.setBrowserUrl(null);
        co.setServerUrl(aliPayCSBDTO.getNotify_url());
        //co.setDraweePhone(eghlRequestDTO.getCustPhone());
        co.setTradeStatus(TradeConstant.TRADE_WAIT);
        //co.setIssuerId(enetsBankRequestDTO.getTxnReq().getMsg().getIssuingBank());
        co.setMd5KeyStr(aliPayCSBDTO.getMd5KeyStr());
        co.setId(aliPayCSBDTO.getOut_trade_no());
        co.setOrderType(AD3Constant.TRADE_ORDER.toString());
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }


        BaseResponse baseResponse = new BaseResponse();
        log.info("-----------------aliPayCSB aliPay线下CSB实体-----------------请求实体 aliPayCSBDTO:{}", JSON.toJSONString(aliPayCSBDTO));
        //把请求参数打包成数组
        Map<String, String> reqmap = new HashMap<String, String>();
        Map<String, String> returnmap = new HashMap<String, String>();
        String extend_params = null;//扩展参数，json
        String sign = null;
        StringBuffer sb = new StringBuffer();
        sb.append("{");//json格式开始
        sb.append("\"secondary_merchant_industry\":\"" + aliPayCSBDTO.getSecondary_merchant_industry() + "\"");
        sb.append(",");
        sb.append("\"secondary_merchant_id\":\"" + aliPayCSBDTO.getSecondary_merchant_id() + "\"");
        sb.append(",");
        sb.append("\"secondary_merchant_name\":\"" + aliPayCSBDTO.getSecondary_merchant_name() + "\"");
        sb.append(",");
        sb.append("\"store_id\":\"" + aliPayCSBDTO.getStore_id() + "\"");
        sb.append(",");
        sb.append("\"store_name\":\"" + aliPayCSBDTO.getStore_name() + "\"");
        sb.append(",");
        if (aliPayCSBDTO.getTerminal_id() != null && !aliPayCSBDTO.getTerminal_id().equals("")) {
            sb.append("\"terminal_id\":\"" + aliPayCSBDTO.getTerminal_id() + "\"");
            sb.append(",");
        }
        if (aliPayCSBDTO.getSys_service_provider_id() != null && !aliPayCSBDTO.getSys_service_provider_id().equals("")) {
            sb.append("\"sys_service_provider_id\":\"" + aliPayCSBDTO.getSys_service_provider_id() + "\"");
            sb.append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));//删除最后一个分号
        sb.append("}");//json格式结尾
        extend_params = sb.toString().trim();//要去掉空格
        log.info("-----------------aliPayCSB aliPay线下CSB实体----------------- extend_params:{}", extend_params);

        reqmap.put("service", aliPayCSBDTO.getService());
        reqmap.put("partner", aliPayCSBDTO.getPartner());
        reqmap.put("_input_charset", aliPayCSBDTO.get_input_charset());
        reqmap.put("notify_url", aliPayCSBDTO.getNotify_url());
        reqmap.put("timestamp", aliPayCSBDTO.getTimestamp());
        reqmap.put("terminal_timestamp", aliPayCSBDTO.getTerminal_timestamp());
        reqmap.put("out_trade_no", aliPayCSBDTO.getOut_trade_no());
        reqmap.put("subject", aliPayCSBDTO.getSubject());
        reqmap.put("product_code", aliPayCSBDTO.getProduct_code());
        reqmap.put("total_fee", aliPayCSBDTO.getTotal_fee());
        reqmap.put("seller_id", aliPayCSBDTO.getSeller_id());
        reqmap.put("seller_email", aliPayCSBDTO.getSeller_email());
        reqmap.put("body", aliPayCSBDTO.getBody());
        reqmap.put("show_url", aliPayCSBDTO.getShow_url());
        reqmap.put("currency", aliPayCSBDTO.getCurrency());
        reqmap.put("trans_currency", aliPayCSBDTO.getTrans_currency());
        reqmap.put("quantity", aliPayCSBDTO.getQuantity());
        reqmap.put("goods_detail", aliPayCSBDTO.getGoods_detail());
        reqmap.put("extend_params", extend_params);
        reqmap.put("it_b_pay", aliPayCSBDTO.getIt_b_pay());
        reqmap.put("passback_parameters", aliPayCSBDTO.getPassback_parameters());
        //请求参数处理和签名
        Map<String, String> signMap = AlipayCore.buildRequestPara(reqmap, aliPayCSBDTO.getMd5KeyStr());
        sign = signMap.get("sign");
        log.info("-----------------aliPayCSB aliPay线下CSB实体----------------- sign:{}", sign);
        NameValuePair[] param = CreateAlipayHttpPostParams(signMap);
        //建立请求
        String url = aliPayCSBDTO.getChannel().getPayUrl() + "_input_charset=" + aliPayCSBDTO.get_input_charset();
        log.info("-----------------aliPayCSB aliPay线下CSB实体----------------- url:{}", url);
        try {
            PostMethod post = new PostMethod(url);
            post.setRequestBody(param);
            HttpClient httpclient = new HttpClient();
            // 设置编码
            httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, aliPayCSBDTO.get_input_charset());
            int stats = 0;
            stats = httpclient.executeMethod(post);
            String respstr = post.getResponseBodyAsString();
            log.info("-----------------CBAlipayPreCreate HttpClient 返回 ----------------- respstr: {}" + respstr);
            if (stats == 200) {
                // 注解方式xml转换为map对象
                Map<String, String> map = XMLUtil.parseAlipayXML(respstr);
                if (null != map) {
                    String is_success = map.get("is_success").toString();//通信结果
                    String error = null;
                    String qrcode = null;
                    if (org.apache.commons.lang3.StringUtils.equals("T", is_success)) {
                        //表示请求通信成功
                        String result_code = map.get("result_code").toString();//业务结果
                        if (org.apache.commons.lang3.StringUtils.equals("SUCCESS", result_code)) {
                            //表示业务处理成功
                            qrcode = map.get("qr_code").toString();
                            if (null != qrcode && !qrcode.equals("")) {
                                //returnmap.put("code", Const.Code.CODE_T000);
                                //returnmap.put("msg", Const.Code.MSG_T000);
                                //returnmap.put("qrCode", qrcode);
                                //dm.setData(map);
                                //dm.setCode(Const.Code.OK);
                                baseResponse.setMsg("success");
                                baseResponse.setCode("200");
                                baseResponse.setData(qrcode);
                            } else {
                                //支付宝返回订单详情解析失败
                                baseResponse.setMsg("fail");
                                baseResponse.setCode("302");
                            }
                        } else {
                            //请求失败
                            error = map.get("detail_error_code").toString();
                            baseResponse.setMsg(error);
                            baseResponse.setCode("302");
                        }
                    } else {
                        //请求失败
                        error = map.get("detail_error_code").toString();
                        baseResponse.setMsg(error);
                        baseResponse.setCode("302");
                    }
                } else {
                    log.info("-----------------alipay查询接口信息转换为实体类为空-----------------");
                }
            } else {
                log.info("-----------------alipay订单查询请求异常，返回状态-----------------"
                        + stats);
            }

        } catch (Exception e) {
            log.info("-----------------CBAlipayPreCreate 支付宝预支付 异常----------------- e:{}" + e);
        }

        return baseResponse;
    }


    /**
     * 支付宝CBAlipayWebsite接口
     *
     * @param aliPayWebDTO 支付宝CBAlipayWebsite接口
     * @return
     */
    @Override
    public BaseResponse aliPayWebsite(AliPayWebDTO aliPayWebDTO) {

        BaseResponse baseResponse = new BaseResponse();
        log.info("-----------------aliPayWebsite aliPayw网站支付实体-----------------请求实体 aliPayCSBDTO:{}", JSON.toJSONString(aliPayWebDTO));

        int num = channelsOrderMapper.selectCountById(aliPayWebDTO.getOut_trade_no());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(aliPayWebDTO.getOut_trade_no());
        } else {
            co = new ChannelsOrder();
        }
        co.setInstitutionOrderId(aliPayWebDTO.getInstitution_order_id());
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
        co.setOrderType(AD3Constant.TRADE_ORDER.toString());
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
        stringBuffer.append("<form method=\"post\" name=\"SendForm\" action=\"" + aliPayWebDTO.getChannel().getPayUrl() + "\">\n");
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

    @Override
    public NameValuePair[] CreateAlipayHttpPostParams(Map<String, String> signMap) {
        log.info("----------------- CreateAlipayHttpPostParams -----------------  CreateAlipayHttpPostParams#时间：" + new Date());
        NameValuePair[] params = null;
        int size = 0;
        try {
            if (signMap != null) {
                size = signMap.size();//初始长度
                params = new NameValuePair[size];
                //第一步先将
                Set set = signMap.keySet();
                Object[] ss = set.toArray();
                for (int i = 0; i < ss.length; i++) {
                    String key = (String) ss[i];
                    String value = signMap.get(key);
                    if (value == null || value.equals("")) {
                        continue;
                    }
                    params[i] = new NameValuePair(key, value);
                }

            } else {
                log.info("----------------- CreateAlipayHttpPostParams -----------------  CBAlipayPreCreate#主要参数为空，时间：" + new Date());
            }
        } catch (Exception e) {
            log.error("----------------- CreateAlipayHttpPostParams -----------------  CBAlipayPreCreate#异常", e);
        }
        return params;
    }

    /**
     * aliPay撤销
     *
     * @param aliPayCancelDTO 撤销实体
     * @return
     */
    @Override
    public BaseResponse alipayCancel(AliPayCancelDTO aliPayCancelDTO) {
        log.info("==================AliPay撤销接口信息记录==================参数记录 aliPayCancelDTO:{}", JSON.toJSONString(aliPayCancelDTO));
        BaseResponse baseResponse = new BaseResponse();
        //默认失败
        baseResponse.setCode(TradeConstant.HTTP_FAIL);
        baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        try {
            //获取调用接口所需参数
            String cancelUrl = aliPayCancelDTO.getChannel().getVoidUrl(); //alipay撤销订单接口请求地址
            //把请求参数打包成数组存入map
            Map<String, String> sParaTemp = new HashMap<>();
            sParaTemp.put("service", aliPayCancelDTO.getService());
            sParaTemp.put("partner", aliPayCancelDTO.getPartner());
            sParaTemp.put("_input_charset", aliPayCancelDTO.get_input_charset());
            sParaTemp.put("out_trade_no", aliPayCancelDTO.getOut_trade_no());
            sParaTemp.put("timestamp", aliPayCancelDTO.getTimestamp());
            //待请求参数数组
            Map<String, String> sPara = AlipayCore.buildRequestPara(sParaTemp, aliPayCancelDTO.getMd5KeyStr());
            log.info("==================AliPay撤销接口信息记录==================上报Alipay撤销接口参数记录 sPara:{}", sPara);
            //发送https请求到alipay
            HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
            HttpRequest alipayRequest = new HttpRequest(HttpResultType.BYTES);
            //设置编码集
            alipayRequest.setCharset(aliPayCancelDTO.get_input_charset());
            alipayRequest.setParameters(AlipayCore.generatNameValuePair(sPara));
            alipayRequest.setUrl(cancelUrl + "_input_charset=" + aliPayCancelDTO.get_input_charset());
            HttpResponse alipayResponse = httpProtocolHandler.execute(alipayRequest, "", "");
            log.info("==================AliPay撤销接口信息记录================== 调用Alipay撤销接口响应结果 alipayResponse:{}", alipayResponse);
            if (null != alipayResponse) {
                String strResult = alipayResponse.getStringResult();
                if (!"".equals(strResult)) {
                    //xml字符串转换成map
                    Map<String, String> reParams = XMLUtil.parseAlipayXML(strResult);
                    String alipaySign = reParams.get("sign");//获取支付宝返回的签名
                    //除去数组中的空值和签名参数sign和sign_type
                    Map<String, String> signPara = AlipayCore.paraFilter(sParaTemp);
                    //生成签名结果
                    String mysign = AlipayCore.buildRequestMysign(signPara, aliPayCancelDTO.getMd5KeyStr());
                    //签名与alipay返回的签名一致则通过校验
                    if (mysign.equals(alipaySign)) {
                        if (reParams.get("is_success").equals("T") && reParams.get("result_code").equals("SUCCESS")) {
                            log.info("==================AliPay撤销接口信息记录================== 订单已撤销成功 orderId:{}", aliPayCancelDTO.getOut_trade_no());
                            //撤销成功
                            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                        } else if ((reParams.get("is_success").equals("F") && !reParams.get("error").equals("SYSTEM_ERROR"))
                                || (reParams.get("is_success").equals("T") && reParams.get("result_code").equals("FAIL") && !reParams.get("detail_error_code").equals("SYSTEM_ERROR"))) {
                            //撤销失败
                            log.info("==================AliPay撤销接口信息记录================== 订单已撤销失败 orderId:{}", aliPayCancelDTO.getOut_trade_no());
                        } else {
                            log.info("==================AliPay撤销接口信息记录================== 撤销遇到未知情况 orderId:{}", aliPayCancelDTO.getOut_trade_no());
                        }
                    }
                }
            } else {
                //网络异常或未知错误
                log.info("==================AliPay撤销接口信息记录================== 调用Alipay撤销接口响应结果为空  orderId:{}", aliPayCancelDTO.getOut_trade_no());
            }
        } catch (Exception e) {
            log.info("==================AliPay撤销接口信息记录================== 接口异常", e);
        }
        return baseResponse;
    }
}
