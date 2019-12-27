package com.asianwallets.channels.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.WechatService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.wechat.*;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.utils.SignTools;
import com.asianwallets.common.utils.UUIDHelper;
import com.asianwallets.common.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WechatServiceImpl implements WechatService {

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;


    /**
     * @param wechatQueryDTO 微信查询实体
     * @return
     */
    @Override
    public BaseResponse wechatQuery(WechatQueryDTO wechatQueryDTO) {
        log.info("==================Wechat查询订单接口信息记录================== 请求参数 wechatQueryDTO:{}", JSON.toJSONString(wechatQueryDTO));
        Map<String, String> queryMap = new HashMap<>();//查询接口参数map
        queryMap.put("appid", wechatQueryDTO.getAppid());
        queryMap.put("mch_id", wechatQueryDTO.getMch_id());
        queryMap.put("sub_mch_id", wechatQueryDTO.getSub_mch_id());
        queryMap.put("out_trade_no", wechatQueryDTO.getOut_trade_no());
        queryMap.put("nonce_str", wechatQueryDTO.getNonce_str());
        queryMap.put("sign_type", wechatQueryDTO.getSign_type());
        String queryUrl = wechatQueryDTO.getChannel().getChannelSingleSelectUrl();//wechat查询订单url
        Map<String, String> queryResultMap = signAndPay(queryMap, queryUrl, wechatQueryDTO.getMd5KeyStr());
        BaseResponse baseResponse = new BaseResponse();
        //默认失败
        baseResponse.setCode(TradeConstant.HTTP_FAIL);
        baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        if (queryResultMap == null || queryResultMap.size() == 0) {
            log.info("==================Wechat查询订单接口信息记录================== 返回结果为空");
            return baseResponse;
        }
        String wxQuerySign = queryResultMap.get("sign");
        if (StringUtils.isEmpty(wxQuerySign)) {
            log.info("----------------Wechat查询订单接口信息记录-----------------调用上游查询接口返回的签名为空");
            return baseResponse;
        }
        queryResultMap.remove("sign");
        //对微信渠道返回的参数验签
        String queryTemp = SignTools.getWXSignStr(queryResultMap);
        String myWxQuerySign = SignTools.getWXSign_MD5(queryTemp, wechatQueryDTO.getMd5KeyStr());
        //验签
        if (!wxQuerySign.equals(myWxQuerySign)) {
            log.info("----------------Wechat查询订单接口信息记录-----------------签名不匹配");
            return baseResponse;
        }
        baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
        baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        baseResponse.setData(queryResultMap);
        return baseResponse;
    }

    /**
     * wechat线下BSC收单方法
     *
     * @param wechatBSCDTO WECHAT线下BSC实体
     * @return
     */
    @Override
    public BaseResponse wechatBSC(WechatBSCDTO wechatBSCDTO) {
        log.info("===============【Wechat线下BSC收单接口信息记录】===============【请求参数】 wechatBSCDTO:{}", JSON.toJSONString(wechatBSCDTO));
        BaseResponse baseResponse = new BaseResponse();
        try {
            Orders orders = wechatBSCDTO.getOrders();
            Channel channel = wechatBSCDTO.getChannel();
            ChannelsOrder channelsOrder = new ChannelsOrder();
            channelsOrder.setMerchantOrderId(orders.getMerchantOrderId());
            channelsOrder.setTradeCurrency(wechatBSCDTO.getFee_type());
            channelsOrder.setTradeAmount(new BigDecimal(wechatBSCDTO.getTotal_fee()));
            channelsOrder.setReqIp(orders.getReqIp());
            channelsOrder.setTradeStatus(TradeConstant.TRADE_WAIT);
            channelsOrder.setMd5KeyStr(channel.getMd5KeyStr());
            channelsOrder.setId(wechatBSCDTO.getOut_trade_no());
            channelsOrder.setOrderType(AD3Constant.TRADE_ORDER);
            channelsOrder.setPayerPhone(orders.getPayerPhone());
            channelsOrder.setPayerName(orders.getPayerName());
            channelsOrder.setPayerBank(orders.getPayerBank());
            channelsOrder.setPayerEmail(orders.getPayerEmail());
            channelsOrder.setBrowserUrl(channel.getNotifyBrowserUrl());
            channelsOrder.setServerUrl(channel.getNotifyServerUrl());
            channelsOrder.setIssuerId(orders.getIssuerId());
            channelsOrder.setMd5KeyStr(channel.getMd5KeyStr());
            channelsOrder.setCreateTime(new Date());
            channelsOrder.setCreator(orders.getCreator());
            channelsOrderMapper.insert(channelsOrder);
            Map<String, String> paramMap = new HashMap<>();//微信支付接口参数map
            paramMap.put("appid", wechatBSCDTO.getAppid());
            paramMap.put("nonce_str", wechatBSCDTO.getNonce_str());
            paramMap.put("mch_id", wechatBSCDTO.getMch_id());
            paramMap.put("sub_mch_id", wechatBSCDTO.getSub_mch_id());
            paramMap.put("sign_type", wechatBSCDTO.getSign_type());
            paramMap.put("body", wechatBSCDTO.getBody());
            paramMap.put("out_trade_no", wechatBSCDTO.getOut_trade_no());
            paramMap.put("fee_type", wechatBSCDTO.getFee_type());
            paramMap.put("total_fee", wechatBSCDTO.getTotal_fee());
            paramMap.put("auth_code", wechatBSCDTO.getAuth_code());
            paramMap.put("spbill_create_ip", wechatBSCDTO.getSpbill_create_ip());
            paramMap.put("detail", wechatBSCDTO.getDetail());
            paramMap.put("version", wechatBSCDTO.getVersion());
            Map<String, String> resultMap = signAndPay(paramMap, wechatBSCDTO.getChannel().getPayUrl(), channel.getMd5KeyStr());//微信支付接口结果map
            if (resultMap == null || resultMap.size() == 0) {
                log.info("----------------Wechat线下BSC收单接口信息记录-----------------调用上游支付接口返回结果为空");
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            log.info("----------------Wechat线下BSC收单接口信息记录-----------------微信返回参数 resultMap:{}", resultMap);
            String wxPaySign = resultMap.get("sign");
            resultMap.remove("sign");
            //对微信渠道返回的参数验签
            String temp = SignTools.getWXSignStr(resultMap);
            String myPaySign = SignTools.getWXSign_MD5(temp, channel.getMd5KeyStr());
            //Map<String, String> receMap = new HashMap<>();//撤销接口参数map
            Map<String, String> queryMap = new HashMap<>();//查询接口参数map
            Map<String, String> queryResultMap = new HashMap<>();//查询接口结果map
            boolean flag = false;
            int payState = 1;//支付状态 1未付款
            if (wxPaySign.equals(myPaySign)) {
                //验签成功
                if (!resultMap.isEmpty() && resultMap.get("return_code").equals("SUCCESS")) {
                    if (resultMap.get("result_code").equals("SUCCESS")) {
                        log.info("----------------Wechat线下BSC收单接口信息记录-----------------订单已支付成功");
                        payState = 3;//支付成功
                        flag = true;
                    } else {
                        log.info("----------------Wechat线下BSC收单接口信息记录-----------------订单为交易中,等待用户输入密码");
                        if (resultMap.get("err_code").equals("USERPAYING")) {
                            //用户支付中，需要输入密码，等待10秒，然后调用被扫订单结果查询API，查询当前订单的不同状态，决定下一步的操作。
                            for (int i = 0; i < 3; i++) {
                                queryMap.put("appid", wechatBSCDTO.getAppid());
                                queryMap.put("mch_id", wechatBSCDTO.getMch_id());
                                queryMap.put("sub_mch_id", wechatBSCDTO.getSub_mch_id());
                                queryMap.put("out_trade_no", wechatBSCDTO.getOut_trade_no());
                                queryMap.put("nonce_str", wechatBSCDTO.getNonce_str());
                                queryMap.put("sign_type", wechatBSCDTO.getSign_type());
                                String queryUrl = wechatBSCDTO.getChannel().getChannelSingleSelectUrl();//wechat查询订单url
                                Thread.sleep(10000); //等待10秒
                                queryResultMap = signAndPay(queryMap, queryUrl, channel.getMd5KeyStr());
                                if (queryResultMap == null || queryResultMap.size() == 0) {
                                    log.info("----------------Wechat线下BSC收单接口信息记录-----------------调用上游查询接口返回结果为空");
                                    baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                                    baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                                    return baseResponse;
                                }
                                String wxQuerySign = queryResultMap.get("sign");
                                if (StringUtils.isEmpty(wxQuerySign)) {
                                    log.info("----------------Wechat线下BSC收单接口信息记录-----------------调用上游查询接口返回的签名为空");
                                    baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                                    baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                                    return baseResponse;
                                }
                                queryResultMap.remove("sign");
                                //对微信渠道返回的参数验签
                                String queryTemp = SignTools.getWXSignStr(queryResultMap);
                                String myWxQuerySign = SignTools.getWXSign_MD5(queryTemp, channel.getMd5KeyStr());
                                if (wxQuerySign.equals(myWxQuerySign)) {
                                    if (queryResultMap.get("return_code").equals("SUCCESS")) {
                                        if (queryResultMap.get("result_code").equals("SUCCESS") && queryResultMap.get("trade_state").equals("SUCCESS")) {
                                            log.info("----------------Wechat线下BSC收单接口信息记录-----------------调用上游查询接口返回结果为 订单交易成功");
                                            flag = true;
                                            payState = 3;//支付成功
                                            break;
                                        } else {
                                            log.info("----------------Wechat线下BSC收单接口信息记录-----------------用户仍在输入密码中");
                                            payState = 2;//支付失败
                                        }
                                    } else {
                                        log.info("----------------Wechat线下BSC收单接口信息记录-----------------用户仍在输入密码中");
                                        payState = 2;//支付失败
                                    }
                                }
                            }
                            log.info("@@@@@@@@@@@@@@@微信BSC超时@@@@@@@@@@@@@@");
                            if (payState != 3) {
                                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                                return baseResponse;
                            }
                        } else if (resultMap.get("err_code").equals("SYSTEMERROR") || resultMap.get("err_code").equals("BANKERROR")) {
                            log.info("----------------Wechat线下BSC收单接口信息记录-----------------上游返回系统错误,继续调用订单查询接口");
                            //系统错误，隔5秒再查询一次
                            Thread.sleep(5000); //等待5秒
                            queryMap.put("appid", wechatBSCDTO.getAppid());
                            queryMap.put("mch_id", wechatBSCDTO.getMch_id());
                            queryMap.put("sub_mch_id", wechatBSCDTO.getSub_mch_id());
                            queryMap.put("out_trade_no", wechatBSCDTO.getOut_trade_no());
                            queryMap.put("nonce_str", wechatBSCDTO.getNonce_str());
                            queryMap.put("sign_type", wechatBSCDTO.getSign_type());
                            String queryUrl = wechatBSCDTO.getChannel().getChannelSingleSelectUrl();//wechat查询订单url
                            queryResultMap = signAndPay(queryMap, queryUrl, channel.getMd5KeyStr());
                            if (queryResultMap == null || queryResultMap.size() == 0) {
                                log.info("------------------Wechat线下BSC收单接口信息记录------------------调用上游查询接口返回结果为空");
                                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                                return baseResponse;
                            }
                            String wxQuerySign = queryResultMap.get("sign");
                            if (StringUtils.isEmpty(wxQuerySign)) {
                                log.info("----------------Wechat线下BSC收单接口信息记录-----------------调用上游查询接口返回的签名为空");
                                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                                return baseResponse;
                            }
                            queryResultMap.remove("sign");
                            //对微信渠道返回的参数验签
                            String queryTemp = SignTools.getWXSignStr(queryResultMap);
                            String myWxQuerySign = SignTools.getWXSign_MD5(queryTemp, channel.getMd5KeyStr());
                            //验签通过
                            if (wxQuerySign.equals(myWxQuerySign)) {
                                if (queryResultMap != null && queryResultMap.get("return_code").equals("SUCCESS")) {
                                    if (queryResultMap.get("result_code").equals("SUCCESS") && queryResultMap.get("trade_state").equals("SUCCESS")) {
                                        flag = true;
                                        payState = 3;//支付成功
                                    } else {
                                        payState = 2;//支付失败
                                    }
                                } else {
                                    payState = 2;//支付失败
                                }
                            }
                        } else {
                            //支付结果失败
                            payState = 2;//支付失败
                            log.info("BSC支付失败:" + resultMap.get("err_code"));
                        }
                    }
                } else {
                    log.info("----------------Wechat线下BSC收单接口信息记录----------------- BSC支付失败 return_code:{}", resultMap.get("return_code"));
                    payState = 2;//支付失败
                }
                //判断交易状态
                if (!flag) {
//                    log.info("----------------Wechat线下BSC收单接口信息记录-----------------支付最终结果失败,需要撤销@@@@@@@@@@@@@@");
//                    //调用撤销接口
//                    receMap.put("appid", wechatBSCDTO.getAppid());
//                    receMap.put("mch_id", wechatBSCDTO.getMch_id());
//                    receMap.put("sub_mch_id", wechatBSCDTO.getSub_mch_id());
//                    receMap.put("out_trade_no", wechatBSCDTO.getOut_trade_no());
//                    receMap.put("nonce_str", wechatBSCDTO.getNonce_str());
//                    receMap.put("sign_type", wechatBSCDTO.getSign_type());
//                    log.info("-----------------Wechat线下BSC收单接口信息记录------------------调用撤销接口参数记录 receMap:{}", receMap);
//                    //撤销接口，需要证书
//                    receMap = reverse(receMap, wechatBSCDTO.getMd5KeyStr());
//                    if (receMap != null) {
//                        String wxReceSign = receMap.get("sign");
//                        receMap.remove("sign");
//                        //对微信渠道返回的参数验签
//                        String receTemp = SignTools.getWXSignStr(receMap);
//                        String myReceSign = SignTools.getWXSign_MD5(receTemp, wechatBSCDTO.getMd5KeyStr());
//                        if (wxReceSign.equals(myReceSign)) {
//                            if (receMap.get("return_code").equals("SUCCESS")) {//响应成功
//                                if (receMap.get("result_code").equals("SUCCESS")) {//撤销成功
//                                    log.info("----------------Wechat线下BSC收单接口信息记录-----------------@@@@@@@@@@@@@@@@@撤销成功@@@@@@@@@@@@@@@");
//                                    payState = 2;//撤销成功，订单状态改为支付失败
//                                } else {
//                                    log.info("----------------Wechat线下BSC收单接口信息记录-----------------@@@@@@@@@@@@@@@@@撤销失败@@@@@@@@@@@@@@@");
//                                    //撤销失败，状态改为未知
//                                    payState = 4;//未知状态
//                                }
//                            }
//                        }
//                    } else {
//                        log.info("----------------Wechat线下BSC收单接口信息记录-----------------@@@@@@@@@@@@@@@@@撤销失败@@@@@@@@@@@@@@@");
//                        payState = 4;
//                    }
                    //支付失败
                    log.info("----------------Wechat线下BSC收单接口信息记录-----------------订单已支付失败");
                    baseResponse.setCode("302");
                    baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                    return baseResponse;
                } else {
                    //支付成功
                    log.info("----------------Wechat线下BSC收单接口信息记录-----------------订单已支付成功");
                    String channelNumber = "";//通道流水号
                    if (resultMap.get("return_code").equals("SUCCESS") && resultMap.get("result_code").equals("SUCCESS")) {
                        channelNumber = resultMap.get("transaction_id");
                    } else {
                        if (queryResultMap.get("return_code").equals("SUCCESS") && queryResultMap.get("result_code").equals("SUCCESS") && queryResultMap.get("trade_state").equals("SUCCESS")) {
                            channelNumber = queryResultMap.get("transaction_id");
                        }
                    }
                    baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                    baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                    baseResponse.setData(channelNumber);
                    return baseResponse;
                }
            } else {
                log.info("----------------Wechat线下BSC收单接口信息记录-----------------验签失败,订单已支付失败");
                baseResponse.setCode("302");
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
        } catch (Exception e) {
            log.error("----------------Wechat线下BSC收单接口信息记录-----------------接口异常", e);
            baseResponse.setCode("302");
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }


    /**
     * 签名和上报
     *
     * @param map    请求参数
     * @param url    请求路径
     * @param Md5key MD5key
     * @return
     */
    private Map<String, String> signAndPay(Map<String, String> map, String url, String Md5key) {
        Map<String, String> resultMap;
        try {
            log.info("----------------调用Wechat接口请求参数记录-----------------paramMap:{}", map);
            //商户Key
            String apikey = Md5key;
            String signTemp = SignTools.getWXSignStr(map);
            String wxSign = SignTools.getWXSign_MD5(signTemp, apikey);
            if (!StringUtils.isEmpty(wxSign)) {
                map.put("sign", wxSign);
            }
            //第二步，将请求参数拼装成XML字符串
            String xmlstr = UUIDHelper.map2XMLForWeChat(map);
            //第三步，将xml参数post到指定接口
            HttpPost httpPost = new HttpPost(url);
            //添加参数
            StringEntity entity = new StringEntity(xmlstr, "UTF-8");
            entity.setContentType("application/xml");
            httpPost.setEntity(entity);
            HttpClient client = HttpClients.createDefault();
            HttpResponse resp = client.execute(httpPost);
            String resData = EntityUtils.toString(resp.getEntity(), "UTF-8");
            resultMap = UUIDHelper.xml2MapForWeChat(resData);
            log.info("----------------调用Wechat接口返回结果记录-----------------resultMap:{}", resultMap);
        } catch (Exception e) {
            log.info("----------------Wechat签名支付方法信息记录-----------------发生异常", e);
            return null;
        }
        return resultMap;
    }

    /**
     * 撤销
     *
     * @param map    请求参数
     * @param md5Key md5Key
     * @return
     */
    private Map<String, String> reverse(Map<String, String> map, String md5Key) throws IOException {
        //        FileInputStream instream = new FileInputStream(new File("/usr/CBPayFiles/ITS/channelcert/" + map.get("mch_id") + ".p12"));
        //        try {
        //            KeyStore keyStore = KeyStore.getInstance("PKCS12");
        //            keyStore.load(instream, map.get("mch_id").toCharArray());
        //        } catch (Exception e) {
        //            log.info("----------------Wechat撤销方法信息 jilu-----------------发生异常", e);
        //        } finally {
        //            instream.close();
        //        }
        //        // Trust own CA and all self-signed certs
        //        SSLContext sslcontext = SSLContexts.custom()
        //                .loadKeyMaterial(keyStore, map.get("mch_id").toCharArray())
        //                .build();
        //        // Allow TLSv1 protocol only
        //        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
        //                sslcontext,
        //                new String[]{"TLSv1"},
        //                null,
        //                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        //        CloseableHttpClient httpclient = HttpClients.custom()
        //                .setSSLSocketFactory(sslsf)
        //                .build();
        //
        //        Map<String, String> resultMap = new HashMap<String, String>();
        //        try {
        //            //商户Key
        //            String apikey = pc.getMD5keyStr();
        //            signTools stool = new signTools();
        //            String signTemp = stool.getWXSignStr(map);
        //            logger.info("排序之后:" + map);
        //            String wxsign = stool.getWXSign_MD5(signTemp, apikey);
        //            if (wxsign != null && !wxsign.equals("")) {
        //                map.put("sign", wxsign);
        //            }
        //            //第二步，将请求参数拼装成XML字符串
        //            String xmlstr = UUIDHelper.map2XMLForWeChat(map);
        //            //第三步，将xml参数post到指定接口
        //            HttpPost httpPost = new HttpPost(pc.getReversal());
        //            //添加参数
        //            StringEntity entity = new StringEntity(xmlstr, "UTF-8");
        //            entity.setContentType("application/xml");
        //            httpPost.setEntity(entity);
        //            HttpResponse resp = httpclient.execute(httpPost);
        //            String resData = EntityUtils.toString(resp.getEntity(), "UTF-8");
        //            remap = UUIDHelper.xml2MapForWeChat(resData);
        //        } catch (Exception e) {
        //            return null;
        //        }
        //        return remap;
        return null;
    }

    /**
     * wechat线下CSB收单方法
     *
     * @param wechatCSBDTO WECHAT线下CSB实体
     * @return
     */
    @Override
    public BaseResponse wechatCSB(WechatCSBDTO wechatCSBDTO) {
        log.info("==================【Wechat线下CSB收单接】==================【请求参数】 wechatCSBDTO:{}", JSON.toJSONString(wechatCSBDTO));
        BaseResponse baseResponse = new BaseResponse();
        try {
            Orders orders = wechatCSBDTO.getOrders();
            Channel channel = wechatCSBDTO.getChannel();
            ChannelsOrder channelsOrder = new ChannelsOrder();
            channelsOrder.setMerchantOrderId(orders.getMerchantOrderId());
            channelsOrder.setTradeCurrency(wechatCSBDTO.getFee_type());
            channelsOrder.setTradeAmount(new BigDecimal(wechatCSBDTO.getTotal_fee()));
            channelsOrder.setReqIp(orders.getReqIp());
            channelsOrder.setTradeStatus(TradeConstant.TRADE_WAIT);
            channelsOrder.setId(wechatCSBDTO.getOut_trade_no());
            channelsOrder.setMd5KeyStr(channel.getMd5KeyStr());
            channelsOrder.setServerUrl(wechatCSBDTO.getNotify_url());
            channelsOrder.setPayerPhone(orders.getPayerPhone());
            channelsOrder.setPayerName(orders.getPayerName());
            channelsOrder.setPayerBank(orders.getPayerBank());
            channelsOrder.setPayerEmail(orders.getPayerEmail());
            channelsOrder.setOrderType(AD3Constant.TRADE_ORDER);
            channelsOrder.setBrowserUrl(channel.getNotifyBrowserUrl());
            channelsOrder.setServerUrl(channel.getNotifyServerUrl());
            channelsOrder.setIssuerId(orders.getIssuerId());
            channelsOrder.setMd5KeyStr(channel.getMd5KeyStr());
            channelsOrder.setCreateTime(new Date());
            channelsOrder.setCreator(orders.getCreator());
            channelsOrderMapper.insert(channelsOrder);
            Map<String, String> paramMap = new HashMap<>();//微信支付接口参数map
            paramMap.put("appid", wechatCSBDTO.getAppid());
            paramMap.put("nonce_str", wechatCSBDTO.getNonce_str());
            paramMap.put("mch_id", wechatCSBDTO.getMch_id());
            paramMap.put("sub_mch_id", wechatCSBDTO.getSub_mch_id());
            paramMap.put("sign_type", wechatCSBDTO.getSign_type());
            paramMap.put("body", wechatCSBDTO.getBody());
            paramMap.put("out_trade_no", wechatCSBDTO.getOut_trade_no());
            paramMap.put("fee_type", wechatCSBDTO.getFee_type());
            paramMap.put("total_fee", wechatCSBDTO.getTotal_fee());
            paramMap.put("spbill_create_ip", wechatCSBDTO.getSpbill_create_ip());
            paramMap.put("time_expire", wechatCSBDTO.getTime_expire());
            paramMap.put("notify_url", wechatCSBDTO.getNotify_url());
            paramMap.put("trade_type", wechatCSBDTO.getTrade_type());
            paramMap.put("detail", wechatCSBDTO.getDetail());
            paramMap.put("version", wechatCSBDTO.getVersion());
            Map<String, String> resultMap = signAndPay(paramMap, wechatCSBDTO.getChannel().getPayUrl(), channel.getMd5KeyStr());
            if (resultMap == null || resultMap.size() == 0) {
                log.info("----------------Wechat线下CSB收单接口信息记录-----------------调用上游支付接口返回结果为空");
                baseResponse.setCode("302");
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            String wxPaySign = paramMap.get("sign");
            if (StringUtils.isEmpty(wxPaySign)) {
                log.info("----------------Wechat线下CSB收单接口信息记录-----------------签名为空");
                baseResponse.setCode("302");
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            paramMap.remove("sign");
            String myPaySign = "";
            String temp = SignTools.getWXSignStr(paramMap);
            //对微信渠道返回的参数验签
            if (paramMap.get("sign_type").equals("HMAC-SHA256")) {
                myPaySign = SignTools.getWXSign_HMACSHA256(temp, channel.getMd5KeyStr());
            } else {
                myPaySign = SignTools.getWXSign_MD5(temp, channel.getMd5KeyStr());
            }
            if (wxPaySign.equals(myPaySign)) {
                //验签通过
                if (resultMap.get("return_code").equals("SUCCESS") && resultMap.get("result_code").equals("SUCCESS")) {
                    String code_url = resultMap.get("code_url");
                    baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                    baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                    baseResponse.setData(code_url);
                    //map1.put("codeUrlTimeOut", codeUrlTimeOut);//支付结束时间
                } else {
                    //返回失败
                    baseResponse.setCode("302");
                    baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                }
            } else {
                log.info("----------------Wechat线下CSB收单接口信息记录-----------------验签失败 wxPaySign:{},myPaySign:{}", wxPaySign, myPaySign);
            }
        } catch (Exception e) {
            log.error("----------------Wechat线下CSB收单接口信息记录-----------------接口异常", e);
            baseResponse.setCode("302");
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        }
        return baseResponse;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/6/26
     * @Descripate 微信退款接口
     **/
    @Override
    public BaseResponse wechatRefund(WechaRefundDTO wechaRefundDTO) {

        ChannelsOrder co = new ChannelsOrder();
        co.setMerchantOrderId(wechaRefundDTO.getOut_trade_no());
        co.setTradeCurrency(wechaRefundDTO.getRefund_fee_type());
        co.setTradeAmount(new BigDecimal(wechaRefundDTO.getRefund_fee()));
        //co.setReqIp(msg.get("ipAddress").toString());
        //co.setDraweeName(eghlRequestDTO.getCustName());
        //co.setDraweeEmail(eghlRequestDTO.getCustEmail());
        //co.setBrowserUrl(msg.get("b2sTxnEndURL").toString());
        //co.setServerUrl(msg.get("s2sTxnEndURL").toString());
        //co.setDraweePhone(eghlRequestDTO.getCustPhone());
        co.setTradeStatus(Byte.valueOf(TradeConstant.TRADE_WAIT));
        //co.setIssuerId(enetsBankRequestDTO.getTxnReq().getMsg().getIssuingBank());
        co.setMd5KeyStr(wechaRefundDTO.getApikey());
        co.setId(wechaRefundDTO.getOut_refund_no());
        co.setOrderType(Byte.valueOf(AD3Constant.REFUND_ORDER));
        co.setCreateTime(new Date());
        channelsOrderMapper.insert(co);


        BaseResponse baseResponse = new BaseResponse();
        Map<String, String> smap = new HashMap<String, String>();//用来签名使用的map
        Map<String, String> returnmap = new HashMap<String, String>();//返回信息map
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //这里的FILE PATH 使用extend5字段
            FileInputStream instream = new FileInputStream(new File(wechaRefundDTO.getChannel().getExtend5() + wechaRefundDTO.getMch_id() + ".p12"));
            try {
                keyStore.load(instream, wechaRefundDTO.getMch_id().toCharArray());
            } finally {
                instream.close();
            }
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, wechaRefundDTO.getMch_id().toCharArray())
                    .build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();

            //smap.put("sub_appid", wechaRefundDTO.getSub_appid());//子商户公众账号ID
            smap.put("refund_account", wechaRefundDTO.getRefund_account());//退款资金来源
            smap.put("appid", wechaRefundDTO.getAppid());//公众账号ID
            smap.put("mch_id", wechaRefundDTO.getMch_id());//商户号
            smap.put("sub_mch_id", wechaRefundDTO.getSub_mch_id());//子商户号
            smap.put("nonce_str", wechaRefundDTO.getNonce_str());//随机字符串
            smap.put("sign_type", wechaRefundDTO.getSign_type());//签名类型
            smap.put("transaction_id", wechaRefundDTO.getTransaction_id());//微信订单号
            smap.put("out_trade_no", wechaRefundDTO.getOut_trade_no());//商户订单号
            smap.put("out_refund_no", wechaRefundDTO.getOut_refund_no());//商户退款单号
            smap.put("total_fee", wechaRefundDTO.getTotal_fee() + "");//标价金额
            smap.put("refund_fee", wechaRefundDTO.getRefund_fee() + "");//退款金额
            smap.put("refund_fee_type", wechaRefundDTO.getRefund_fee_type());//退款币种
            smap.put("refund_desc", wechaRefundDTO.getRefund_desc());//退款原因

            String signTemp = SignTools.getSignWithSymbol(smap);
            log.info("排序之后:" + smap);
            String wxsign = null;
            if (wechaRefundDTO.getSign_type().equals("HMAC-SHA256")) {
                wxsign = SignTools.getWXSign_HMACSHA256(signTemp, wechaRefundDTO.getApikey());
                smap.put("sign", wxsign);
            } else {
                wxsign = SignTools.getWXSign_MD5(signTemp, wechaRefundDTO.getApikey());
                smap.put("sign", wxsign);
            }
            //第二步，将请求参数拼装成XML字符串
            String xmlstr = XMLUtil.map2XMLForWeChat(smap);
            log.info("------------------- 微信退款接口 -------------------xmlstr:" + xmlstr);
            if (xmlstr != null && !xmlstr.equals("")) {
                //第三步，将xml参数post到指定接口
                HttpPost httpPost = new HttpPost(wechaRefundDTO.getChannel().getRefundUrl());
                //添加参数
                StringEntity entity = new StringEntity(xmlstr, "UTF-8");
                entity.setContentType("application/xml");
                httpPost.setEntity(entity);
                HttpResponse resp = httpclient.execute(httpPost);
                if (resp != null && resp.getStatusLine().getStatusCode() == 200) {
                    String resData = EntityUtils.toString(resp.getEntity(), "UTF-8");
                    if (resData != null && !resData.equals("")) {
                        log.info("------------------- 微信退款接口 --------------------- resData:{}", resData);
                        returnmap = XMLUtil.xml2MapForWeChat(resData);
                        if (returnmap != null && returnmap.get("return_code") != null && returnmap.get("return_code").equals("SUCCESS")
                                && returnmap.get("result_code") != null && !returnmap.get("result_code").equals("") && returnmap.get("result_code").equals("SUCCESS")) {
                            baseResponse.setCode("200");
                            baseResponse.setMsg("success");
                            baseResponse.setData(returnmap);
                            channelsOrderMapper.updateStatusById(wechaRefundDTO.getOut_refund_no(), returnmap.get("refund_id"), "2");
                        } else {
                            //微信请求退款通信失败
                            log.info("------------------- 微信退款接口 失败 ------------------- returnmap:{}", returnmap);
                            baseResponse.setCode("200");
                            baseResponse.setMsg("fail");
                            channelsOrderMapper.updateStatusById(wechaRefundDTO.getOut_refund_no(), returnmap.get("refund_id"), "3");
                        }
                    } else {
                        //微信请求退款通信失败
                        log.info("------------------- 微信退款接口 失败 ------------------- resData:{}", resData);
                        baseResponse.setCode("200");
                        baseResponse.setMsg("fail");
                    }
                } else {
                    //微信请求退款通信失败
                    log.info("------------------- 微信退款接口 失败 ------------------- status:{}", resp.getStatusLine().getStatusCode());
                    baseResponse.setCode("302");
                    baseResponse.setMsg("fail");
                }
            } else {
                //微信请求参数格式成xml字符串后为空
                log.info("------------------- 微信退款接口 -------------------将请求参数拼装成XML字符串 xmlstr:{}", xmlstr);
                baseResponse.setCode("302");
                baseResponse.setMsg("fail");
            }
        } catch (Exception e) {
            log.info("------------------- 微信退款接口 -------------------Exception :{}", e);
            baseResponse.setCode("302");
            baseResponse.setMsg("fail");
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author XuWenQi
     * @Date 2019/7/1
     * @Descripate 微信撤销接口
     **/
    @Override
    public BaseResponse wechatCancel(WechatCancelDTO wechatCancelDTO) {
        KeyStore keyStore = null;
        //这里的FILE PATH 使用extend5字段
        try (FileInputStream instream = new FileInputStream(wechatCancelDTO.getChannel().getExtend5() + wechatCancelDTO.getChannelMerchantId() + ".p12")) {
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(instream, wechatCancelDTO.getChannelMerchantId().toCharArray());
        } catch (Exception e) {
            log.info("----------------Wechat撤销接口信息记录----------------- 读取证书文件发生异常", e);
        }
        CloseableHttpClient httpclient = null;
        try {
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, wechatCancelDTO.getChannelMerchantId().toCharArray())
                    .build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
        } catch (Exception e) {
            log.info("----------------Wechat撤销接口信息记录----------------- 接口异常", e);
        }
        Map<String, String> map = new HashMap<>();
        map.put("appid", wechatCancelDTO.getAppid());
        map.put("mch_id", wechatCancelDTO.getMch_id());
        map.put("sub_mch_id", wechatCancelDTO.getSub_mch_id());
        map.put("out_trade_no", wechatCancelDTO.getOut_trade_no());
        map.put("nonce_str", wechatCancelDTO.getNonce_str());
        map.put("sign_type", wechatCancelDTO.getSign_type());
        Map<String, String> resultMap = new HashMap<>();
        BaseResponse baseResponse = new BaseResponse();
        //默认失败
        baseResponse.setCode(TradeConstant.HTTP_FAIL);
        baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        try {
            //商户Key
            String apikey = wechatCancelDTO.getMd5KeyStr();
            String signTemp = SignTools.getWXSignStr(map);
            log.info("----------------Wechat撤销接口信息记录----------------- 排序之后map:{}", map);
            String wxsign = SignTools.getWXSign_MD5(signTemp, apikey);
            if (!StringUtils.isEmpty(wxsign)) {
                map.put("sign", wxsign);
            }
            //第二步，将请求参数拼装成XML字符串
            String xmlstr = UUIDHelper.map2XMLForWeChat(map);
            //第三步，将xml参数post到指定接口
            HttpPost httpPost = new HttpPost(wechatCancelDTO.getChannel().getVoidUrl());
            //添加参数
            StringEntity entity = new StringEntity(xmlstr, "UTF-8");
            entity.setContentType("application/xml");
            httpPost.setEntity(entity);
            HttpResponse resp = httpclient.execute(httpPost);
            String resData = EntityUtils.toString(resp.getEntity(), "UTF-8");
            resultMap = UUIDHelper.xml2MapForWeChat(resData);
            if (resultMap == null || resultMap.size() == 0) {
                log.info("----------------Wechat撤销接口信息记录----------------- 调用微信撤销接口返回结果为空");
                return baseResponse;
            }
            log.info("----------------Wechat撤销接口信息记录----------------- 调用微信撤销接口返回结果记录 resultMap:{}", resultMap);
            String wxSign = resultMap.get("sign");
            resultMap.remove("sign");
            //对微信渠道返回的参数验签
            String temp = SignTools.getWXSignStr(resultMap);
            String mySign = SignTools.getWXSign_MD5(temp, wechatCancelDTO.getMd5KeyStr());
            if (wxSign.equals(mySign)) {
                if (resultMap.get("return_code").equals("SUCCESS")) {//响应成功
                    if (resultMap.get("result_code").equals("SUCCESS")) {
                        log.info("----------------Wechat撤销接口信息记录----------------- 撤销成功");
                        //撤销成功
                        baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                        baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                    } else {
                        log.info("----------------Wechat撤销接口信息记录----------------- 撤销失败");
                    }
                }
            } else {
                log.info("----------------Wechat撤销接口信息记录----------------- 签名不匹配");
            }
        } catch (Exception e) {
            log.info("----------------Wechat撤销接口信息记录----------------- 接口异常", e);
        }
        return baseResponse;
    }
}
