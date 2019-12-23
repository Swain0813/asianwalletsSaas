package com.asianwallets.channels.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.MegaPayService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.megapay.*;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.HttpClientUtils;
import com.asianwallets.common.utils.MD5;
import com.asianwallets.common.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-05-30 15:09
 **/
@Service
@Slf4j
public class MegaPayServiceImpl implements MegaPayService {

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate megaPayTHB收单接口
     **/
    @Override
    public BaseResponse megaPayTHB(MegaPayRequestDTO megaPayRequestDTO) {
        log.info("=============【MegaPayTHB收单接口】===============【请求参数】 megaPayRequestDTO:{}", JSON.toJSONString(megaPayRequestDTO));
        BaseResponse response = new BaseResponse();
        String body = null;
        try {
            int num = channelsOrderMapper.selectCountById(megaPayRequestDTO.getOrderID());
            ChannelsOrder co;
            if (num > 0) {
                co = channelsOrderMapper.selectByPrimaryKey(megaPayRequestDTO.getOrderID());
            } else {
                co = new ChannelsOrder();
            }
            co.setMerchantOrderId(megaPayRequestDTO.getInstitutionOrderId());
            co.setTradeCurrency(megaPayRequestDTO.getTradeCurrency());
            co.setTradeAmount(new BigDecimal(megaPayRequestDTO.getAmt()));
            co.setReqIp(megaPayRequestDTO.getReqIp());
            co.setPayerName(megaPayRequestDTO.getC_Name());
            co.setBrowserUrl(megaPayRequestDTO.getRetURL());
            co.setTradeStatus(Byte.valueOf(TradeConstant.TRADE_WAIT));
            co.setIssuerId(megaPayRequestDTO.getBMode());
            co.setMd5KeyStr(megaPayRequestDTO.getMd5KeyStr());
            co.setId(megaPayRequestDTO.getOrderID());
            co.setOrderType(Byte.valueOf(AD3Constant.TRADE_ORDER));
            if (num > 0) {
                co.setUpdateTime(new Date());
                channelsOrderMapper.updateByPrimaryKeySelective(co);
            } else {
                co.setCreateTime(new Date());
                channelsOrderMapper.insert(co);
            }
            cn.hutool.http.HttpResponse execute = HttpRequest.post(megaPayRequestDTO.getChannel().getPayUrl())
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(BeanToMapUtil.beanToMap(megaPayRequestDTO))
                    .timeout(20000)
                    .execute();
            int status = execute.getStatus();
            body = execute.body();
            log.info("===============【MegaPayTHB收单接口】===============【接口响应参数】 http状态码: {} | body: {}", status, JSON.toJSONString(body));
            //判断HTTP状态码
            if (StringUtils.isEmpty(body)) {
                response.setCode(TradeConstant.HTTP_FAIL);
                response.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return response;
            }
        } catch (HttpException e) {
            log.info("===============【MegaPayTHB收单接口】===============【接口异常】", e);
            response.setCode(TradeConstant.HTTP_FAIL);
            response.setMsg(TradeConstant.HTTP_FAIL_MSG);
            return response;
        }
        response.setCode(TradeConstant.HTTP_SUCCESS);
        response.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        response.setData(body);
        return response;

    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate megaPayIDR收单接口
     **/
    @Override
    public BaseResponse megaPayIDR(MegaPayIDRRequestDTO megaPayIDRRequestDTO) {
        log.info("=============【MegaPayIDR收单接口】===============【请求参数】 megaPayIDRRequestDTO:{}", JSON.toJSONString(megaPayIDRRequestDTO));
        BaseResponse response = new BaseResponse();
        String body = null;
        try {
            int num = channelsOrderMapper.selectCountById(megaPayIDRRequestDTO.getE_inv());
            ChannelsOrder co;
            if (num > 0) {
                co = channelsOrderMapper.selectByPrimaryKey(megaPayIDRRequestDTO.getE_inv());
            } else {
                co = new ChannelsOrder();
            }
            co.setMerchantOrderId(megaPayIDRRequestDTO.getInstitutionOrderId());
            co.setTradeCurrency(megaPayIDRRequestDTO.getTradeCurrency());
            co.setTradeAmount(new BigDecimal(megaPayIDRRequestDTO.getE_amt()));
            co.setReqIp(megaPayIDRRequestDTO.getReqIp());
            co.setPayerName(megaPayIDRRequestDTO.getCusName());
            co.setBrowserUrl(megaPayIDRRequestDTO.getE_respURL());
            co.setTradeStatus(Byte.valueOf(TradeConstant.TRADE_WAIT));
            co.setIssuerId(megaPayIDRRequestDTO.getBMode());
            co.setMd5KeyStr(megaPayIDRRequestDTO.getMd5KeyStr());
            co.setId(megaPayIDRRequestDTO.getE_inv());
            co.setOrderType(Byte.valueOf(AD3Constant.TRADE_ORDER));
            if (num > 0) {
                co.setUpdateTime(new Date());
                channelsOrderMapper.updateByPrimaryKeySelective(co);
            } else {
                co.setCreateTime(new Date());
                channelsOrderMapper.insert(co);
            }
            cn.hutool.http.HttpResponse execute = HttpRequest.post(megaPayIDRRequestDTO.getChannel().getPayUrl())
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(BeanToMapUtil.beanToMap(megaPayIDRRequestDTO))
                    .timeout(20000)
                    .execute();
            int status = execute.getStatus();
            body = execute.body();
            log.info("===============【MegaPayIDR收单接口】===============【接口响应参数】 http状态码: {} | body: {}", status, JSON.toJSONString(body));
            //判断HTTP状态码
            if (StringUtils.isEmpty(body)) {
                response.setCode(TradeConstant.HTTP_FAIL);
                response.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return response;
            }
        } catch (HttpException e) {
            log.info("===============【MegaPayIDR收单接口】===============【接口异常】", e);
            response.setCode(TradeConstant.HTTP_FAIL);
            response.setMsg(TradeConstant.HTTP_FAIL_MSG);
            return response;
        }
        response.setCode(TradeConstant.HTTP_SUCCESS);
        response.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        response.setData(body);
        return response;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate nextPos收单接口
     **/
    @Override
    public BaseResponse nextPosCsb(NextPosRequestDTO nextPosRequestDTO) {
        log.info("==================【NextPos收单接口】==================【请求参数】 nextPosRequestDTO:{}", JSON.toJSONString(nextPosRequestDTO));
        BaseResponse baseResponse = new BaseResponse();
        try {
            Orders orders = nextPosRequestDTO.getOrders();
            Channel channel = nextPosRequestDTO.getChannel();
            ChannelsOrder channelsOrder = new ChannelsOrder();
            channelsOrder.setId(orders.getId());
            channelsOrder.setMerchantOrderId(orders.getMerchantOrderId());
            channelsOrder.setTradeCurrency(orders.getTradeCurrency());
            channelsOrder.setTradeAmount(new BigDecimal(nextPosRequestDTO.getAmt()));
            channelsOrder.setReqIp(orders.getReqIp());
            channelsOrder.setServerUrl(nextPosRequestDTO.getChannel().getNotifyServerUrl());
            channelsOrder.setTradeStatus(TradeConstant.TRADE_WAIT);
            channelsOrder.setIssuerId(channel.getIssuerId());
            channelsOrder.setOrderType(AD3Constant.TRADE_ORDER);
            channelsOrder.setMd5KeyStr(channel.getMd5KeyStr());
            channelsOrder.setPayerPhone(orders.getPayerPhone());
            channelsOrder.setPayerName(orders.getPayerName());
            channelsOrder.setPayerBank(orders.getPayerBank());
            channelsOrder.setPayerEmail(orders.getPayerEmail());
            channelsOrder.setCreateTime(new Date());
            channelsOrder.setCreator(orders.getCreator());
            channelsOrderMapper.insert(channelsOrder);
            Map<String, Object> paramMap = new HashMap<>(5);
            //商户号
            paramMap.put("merID", nextPosRequestDTO.getMerID());
            //订单号
            paramMap.put("einv", nextPosRequestDTO.getEinv());
            //金额
            paramMap.put("amt", nextPosRequestDTO.getAmt());
            //产品名
            paramMap.put("product", nextPosRequestDTO.getProduct());
            paramMap.put("return_url", nextPosRequestDTO.getReturn_url());
            log.info("==================【NextPos收单接口】==================【NextPos接口请求参数】 paramMap: {}", JSON.toJSONString(paramMap));
            cn.hutool.http.HttpResponse execute = HttpRequest.post(channel.getPayUrl())
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(paramMap)
                    .timeout(20000)
                    .execute();
            int status = execute.getStatus();
            String body = execute.body();
            log.info("==================【NextPos收单接口】==================【NextPos接口响应参数】【状态码】 status: {} | 【响应参数】 body: {}", status, body);
            if (status != 200 || StringUtils.isEmpty(body)) {
                log.info("==================【NextPos收单接口】==================【响应结果不正确】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            //解析XML响应参数
            Map<String, Object> respMap = XMLUtil.xml2Map(body);
            log.info("==================【NextPos收单接口】==================【解析XML后的参数】 respMap:{}", JSON.toJSONString(respMap));
            //有任何错误就会返回异常信息errMessage,和errDec,不会返回其他字段
            if (respMap.containsKey("errMessage")) {
                log.info("==================【NextPos收单接口】==================【返回参数包含错误信息】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            //二维码字符串
            String qrString = String.valueOf(respMap.get("qrString"));
            if (StringUtils.isEmpty(qrString)) {
                log.info("==================【NextPos收单接口】==================【二维码参数为空】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            //校验接口返回签名
            String mark = respMap.get("mark").toString();
            String encodeStr = new Base64().encodeToString(qrString.getBytes());
            String clearText = encodeStr + channel.getMd5KeyStr() + channel.getPayCode();
            log.info("==================【NextPos收单接口】==================【签名前的明文】 clearText:{}", clearText);
            String mySign = MD5.MD5Encode(clearText).toUpperCase();
            log.info("==================【NextPos收单接口】==================【签名后的密文】 mySign:{}", mySign);
            //判断回签结果并处理
            if (!mySign.equals(mark)) {
                log.info("==================【NextPos收单接口】==================【验证返回签名不通过】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(qrString);
            log.info("==================【NextPos收单接口】==================【出码成功】");
            return baseResponse;
        } catch (Exception e) {
            log.info("==================【NextPos收单接口】==================【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author XuWenQi
     * @Date 2019/8/9
     * @Descripate nextPos查询接口
     **/
    @Override
    public BaseResponse nextPosQuery(NextPosQueryDTO nextPosQueryDTO) {
        log.info("==================【NextPos查询订单】==================【请求参数】 nextPosQueryDTO: {}", JSON.toJSONString(nextPosQueryDTO));
        String nextPosQueryUrl = nextPosQueryDTO.getChannel().getChannelSingleSelectUrl();
        log.info("==================【NextPos查询订单】==================【查询请求URL】 nextPosQueryUrl: {}", nextPosQueryUrl);
        BaseResponse baseResponse = new BaseResponse();
        try {
            cn.hutool.http.HttpResponse execute = HttpRequest.post(nextPosQueryUrl)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(BeanToMapUtil.beanToMap(nextPosQueryDTO))
                    .timeout(10000)
                    .execute();
            int status = execute.getStatus();
            String body = execute.body();
            log.info("==================【NextPos查询订单】================== http状态码: {} | body:{}", status, JSON.toJSONString(body));
            if (status != 200) {
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                return baseResponse;
            }
            //响应Map
            Map<String, Object> respMap = XMLUtil.xml2Map(body);
            log.info("==================【NextPos查询订单】==================【解析后的响应参数】 respMap: {}", JSON.toJSONString(respMap));
            String mark = String.valueOf(respMap.get("mark"));
            //金额格式转换
            DecimalFormat df = new DecimalFormat("#,##0.00");
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(respMap.get("amt")));
            String formatAmt = df.format(bigDecimal);
            byte[] refCodes = cn.hutool.core.codec.Base64.decode(respMap.get("refCode").toString());
            String clearText = new String(refCodes) + respMap.get("einv") + nextPosQueryDTO.getMerRespPassword() +
                    nextPosQueryDTO.getMerRespID() + respMap.get(nextPosQueryDTO.getMerRespID()) + formatAmt;
            log.info("==================【NextPos查询订单】==================【通道响应结果】签名前的明文 clearText: {}", clearText);
            String mySign = MD5.MD5Encode(clearText).toUpperCase();
            log.info("==================【NextPos查询订单】==================【通道响应结果】签名后的密文 mySign: {}", mySign);
            if (!mark.equals(mySign)) {
                log.info("==================【NextPos查询订单】==================【签名不匹配】");
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            //有任何错误就会返回异常信息errMessage,和errDec,不会返回其他字段
            if (respMap.containsKey("errMessage")) {
                log.info("==================【NextPos查询订单】==================【通道响应报文有错误信息】 errMessage: {}", respMap.get("errMessage"));
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(String.valueOf(respMap.get("errMessage")));
                return baseResponse;
            }
            //查询成功
            log.info("==================【NextPos查询订单】==================【查询成功】");
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(respMap);
        } catch (Exception e) {
            log.info("==================【NextPos查询订单】==================【接口异常】", e);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
        }
        return baseResponse;
    }

    /**
     * NextPos退款接口
     *
     * @param nextPosRefundDTO nextPos退款实体
     * @return BaseResponse
     */
    @Override
    public BaseResponse nextPosRefund(NextPosRefundDTO nextPosRefundDTO) {
        //组装签名前的明文
        String requestClearText = nextPosRefundDTO.getTradeNo() + nextPosRefundDTO.getOrderId() + nextPosRefundDTO.getMerRespPassword() +
                nextPosRefundDTO.getMerRespID() + nextPosRefundDTO.getAmt();
        log.info("==================【NextPos退款】==================【请求通道前的明文】 requestClearText: {}", requestClearText);
        String requestSign = MD5.MD5Encode(requestClearText).toUpperCase();
        nextPosRefundDTO.setMark(requestSign);
        log.info("==================【NextPos退款】==================【Channel服务请求参数】 nextPosRefundDTO: {}", JSON.toJSONString(nextPosRefundDTO));
        String nextPosRefundUrl = nextPosRefundDTO.getChannel().getRefundUrl();
        log.info("==================【NextPos退款】==================【NextPos退款请求URL】 nextPosRefundUrl: {}", nextPosRefundUrl);
        BaseResponse baseResponse = new BaseResponse();
        try {
            Map<String, Object> refundMap = new HashMap<>();
            refundMap.put("merID", nextPosRefundDTO.getMerID());
            refundMap.put("orderID", nextPosRefundDTO.getOrderId());
            refundMap.put("refundType", nextPosRefundDTO.getRefundType());
            refundMap.put("originalAmt", nextPosRefundDTO.getOriginalAmt());
            refundMap.put("amt", nextPosRefundDTO.getAmt());
            refundMap.put("tradeNo", nextPosRefundDTO.getTradeNo());
            refundMap.put("mark", nextPosRefundDTO.getMark());
            log.info("==================【NextPos退款】==================【NextPos退款接口请求参数】 requestMap: {}", JSON.toJSONString(refundMap));
            cn.hutool.http.HttpResponse execute = HttpRequest.post(nextPosRefundUrl)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(refundMap)
                    .timeout(30000)
                    .execute();
            int status = execute.getStatus();
            String body = execute.body();
            log.info("==================【NextPos退款】==================【退款接口响应结果】 http状态码: {} | body: {}", status, JSON.toJSONString(body));
            if (status != 200) {
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                return baseResponse;
            }
            //解析xml参数
            Map<String, Object> respMap = XMLUtil.xml2Map(body);
            log.info("==================【NextPos退款】==================【解析后的响应参数】 respMap: {}", JSON.toJSONString(respMap));
            //金额格式转换
            DecimalFormat df = new DecimalFormat("#,##0.00");
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(respMap.get("amt")));
            String formatAmt = df.format(bigDecimal);
            byte[] refCodes = cn.hutool.core.codec.Base64.decode(String.valueOf(respMap.get("refCode")));
            //签名前的明文
            String clearText = new String(refCodes) + respMap.get("einv") + nextPosRefundDTO.getMerRespPassword() +
                    nextPosRefundDTO.getMerRespID() + respMap.get(nextPosRefundDTO.getMerRespID()) + formatAmt;
            log.info("==================【NextPos退款】==================【通道响应结果】签名前的明文 clearText: {}", clearText);
            String mySign = MD5.MD5Encode(clearText).toUpperCase();
            log.info("==================【NextPos退款】==================【通道响应结果】签名后的密文 mySign: {}", mySign);
            //校验通道响应签名
            if (!String.valueOf(respMap.get("mark")).equals(mySign)) {
                log.info("==================【NextPos退款】==================【响应签名不匹配】");
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            //有任何错误就会返回异常信息errMessage,和errDec,不会返回其他字段
            if (respMap.containsKey("errMessage")) {
                log.info("==================【NextPos退款】==================【通道响应报文有错误信息】 errMessage: {}", respMap.get("errMessage"));
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(String.valueOf(respMap.get("errMessage")));
                return baseResponse;
            }
            String refundStatus = String.valueOf(respMap.get(nextPosRefundDTO.getMerRespID()));
            if (!StringUtils.isEmpty(refundStatus) && "000".equals(refundStatus)) {
                log.info("==================【NextPos退款】==================【退款成功】");
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
                baseResponse.setData(respMap);
            } else {
                baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            }
        } catch (Exception e) {
            log.error("==================【NextPos退款】==================【接口异常】", e);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
        }
        return baseResponse;
    }

    /**
     * MegaPay查询接口
     *
     * @param megaPayQueryDTO megaPay查询
     * @return
     */
    @Override
    public BaseResponse megaPayQuery(MegaPayQueryDTO megaPayQueryDTO) {
        log.info("==================【MegaPay查询接口】==================【请求参数】 megaPayQueryDTO: {}", JSON.toJSONString(megaPayQueryDTO));
        BaseResponse baseResponse = new BaseResponse();
        Map<String, Object> resultMap = null;
        try {
            String url = "https://www.megapay.in.th/megaTraxCheck/traxcheck.asmx";
            String param = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <checkTranStatus xmlns=\"http://tempuri.org/\">\n" +
                    "      <invoice>" +
                    megaPayQueryDTO.getInvoice() + "</invoice>\n" +
                    "      <merchantID>" +
                    megaPayQueryDTO.getMerchantID() + "m</merchantID>\n" +
                    "    </checkTranStatus>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";
            String body = HttpClientUtils.reqPostStringByXML(url, param, null);
            resultMap = XMLUtil.xml2Map(body);
            log.info("==================【MegaPay查询接口】==================【解析XML参数后的结果】 resultMap: {}", JSON.toJSONString(resultMap));
            if (resultMap == null || resultMap.size() == 0) {
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
        } catch (Exception e) {
            log.info("==================【MegaPay查询接口】==================【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            return baseResponse;
        }
        baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
        baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        baseResponse.setData(resultMap.get("checkTranStatusResult"));
        return baseResponse;
    }
}
