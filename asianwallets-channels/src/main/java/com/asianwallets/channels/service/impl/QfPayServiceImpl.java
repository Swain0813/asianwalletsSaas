package com.asianwallets.channels.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.config.ChannelsConfig;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.QfPayService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.qfpay.*;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.HttpResponse;
import com.asianwallets.common.utils.HttpClientUtils;
import com.asianwallets.common.utils.MD5;
import com.asianwallets.common.utils.ReflexClazzUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-02-11 11:33
 **/
@Service
@Slf4j
public class QfPayServiceImpl implements QfPayService {

    @Autowired
    private ChannelsConfig channelsConfig;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;


    /**
     * @return
     * @Author YangXu
     * @Date 2020/2/11
     * @Descripate CSB
     **/
    @Override
    public BaseResponse qfPayCSB(QfPayDTO qfPayDTO) {
        log.info("==================【QfPayCSB收单接口】==================【请求参数】 qfPayDTO: {}", JSON.toJSONString(qfPayDTO));
        int num = channelsOrderMapper.selectCountById(qfPayDTO.getOrderId());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(qfPayDTO.getOrderId());
        } else {
            co = new ChannelsOrder();
        }
        //co.setInstitutionOrderId(xenditDTO.getInstitutionOrderId());
        co.setTradeCurrency(qfPayDTO.getChannel().getCurrency());
        co.setTradeAmount(new BigDecimal(qfPayDTO.getQfPayCSBDTO().getCash()));
        co.setReqIp(qfPayDTO.getReqIp());
        //co.setDraweeEmail(xenditDTO.getXenditPayRequestDTO().getPayer_email());
        co.setTradeStatus(TradeConstant.TRADE_WAIT);
        //co.setIssuerId(xenditDTO.getXenditPayRequestDTO().getPayment_methods()[0]);
        co.setMd5KeyStr(qfPayDTO.getChannel().getMd5KeyStr());
        co.setId(qfPayDTO.getOrderId());
        co.setOrderType(AD3Constant.TRADE_ORDER);
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }
        BaseResponse baseResponse = new BaseResponse();
        QfPayCSBDTO qfPayCSBDTO = qfPayDTO.getQfPayCSBDTO();
        //
        String sign = addSign(ReflexClazzUtils.getFieldForStringValue(qfPayCSBDTO), qfPayDTO.getChannel().getMd5KeyStr());
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("x-sign", sign);
        headerMap.put("x-appid", qfPayDTO.getChannel().getChannelMerchantId().split("\\|")[1]);
        //og.info("----------------- qfPayCSB收单接口----------------- url:{};qfPayCSBDTO:{};headMap:{}", channelsConfig.getQfPayCSBUrl(),JSON.toJSONString(qfPayCSBDTO),JSON.toJSONString(map));
        HttpResponse httpResponse = HttpClientUtils.reqPost(channelsConfig.getQfPayCSBUrl(), qfPayCSBDTO, headerMap);
        //log.info("----------------- qfPayCSB收单接口返回----------------- httpResponse:{}", JSON.toJSONString(httpResponse));
        if (httpResponse.getHttpStatus() == 200) {
            QfResDataDTO qfResDataDTO = JSON.parseObject(String.valueOf(httpResponse.getJsonObject()), QfResDataDTO.class);
            if (200 == qfResDataDTO.getCode()) {
                baseResponse.setCode(qfResDataDTO.getCode().toString());
                baseResponse.setData(qfResDataDTO.getData());
            } else {
                baseResponse.setCode("302");
                baseResponse.setData(qfResDataDTO.getData());
            }
        } else {
            baseResponse.setCode("302");
            baseResponse.setMsg(httpResponse.getJsonObject().toJSONString());
        }
        return baseResponse;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2020/2/11
     * @Descripate BSC
     **/
    @Override
    public BaseResponse qfPayBSC(QfPayDTO qfPayDTO) {
        log.info("==================【QfPayBSC收单接口】==================【请求参数】 qfPayDTO: {}", JSON.toJSONString(qfPayDTO));
        int num = channelsOrderMapper.selectCountById(qfPayDTO.getOrderId());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(qfPayDTO.getOrderId());
        } else {
            co = new ChannelsOrder();
        }
        //co.setInstitutionOrderId(xenditDTO.getInstitutionOrderId());
        co.setTradeCurrency(qfPayDTO.getChannel().getCurrency());
        co.setTradeAmount(new BigDecimal(qfPayDTO.getQfPayBSCDTO().getCash()));
        co.setReqIp(qfPayDTO.getReqIp());
        //co.setDraweeEmail(xenditDTO.getXenditPayRequestDTO().getPayer_email());
        co.setTradeStatus(TradeConstant.TRADE_WAIT);
        //co.setIssuerId(xenditDTO.getXenditPayRequestDTO().getPayment_methods()[0]);
        co.setMd5KeyStr(qfPayDTO.getChannel().getMd5KeyStr());
        co.setId(qfPayDTO.getOrderId());
        co.setOrderType(AD3Constant.TRADE_ORDER);
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }
        BaseResponse baseResponse = new BaseResponse();
        QfPayBSCDTO qfPayBSCDTO = qfPayDTO.getQfPayBSCDTO();
        String sign = addSign(ReflexClazzUtils.getFieldForStringValue(qfPayBSCDTO), qfPayDTO.getChannel().getMd5KeyStr());
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("x-sign", sign);
        headerMap.put("x-appid", qfPayDTO.getChannel().getChannelMerchantId().split("\\|")[1]);
        //log.info("----------------- qfPayBSC收单接口----------------- url:{};qfPayCSBDTO:{};headMap:{}", channelsConfig.getQfPayBSCUrl(), JSON.toJSONString(qfPayBSCDTO), JSON.toJSONString(map));
        HttpResponse httpResponse = HttpClientUtils.reqPost(channelsConfig.getQfPayBSCUrl(), qfPayBSCDTO, headerMap);
        //log.info("----------------- qfPayBSC收单接口返回----------------- httpResponse:{}", JSON.toJSONString(httpResponse));
        if (httpResponse.getHttpStatus() == 200) {
            QfResDataDTO qfResDataDTO = JSON.parseObject(String.valueOf(httpResponse.getJsonObject()), QfResDataDTO.class);
            if (200 == qfResDataDTO.getCode()) {
                baseResponse.setCode(qfResDataDTO.getCode().toString());
                baseResponse.setData(qfResDataDTO.getData());
            } else {
                baseResponse.setCode("302");
                baseResponse.setData(qfResDataDTO.getData());
            }
        } else {
            baseResponse.setCode("302");
            baseResponse.setMsg(httpResponse.getJsonObject().toJSONString());
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/2/11
     * @Descripate Query
     **/
    @Override
    public BaseResponse qfPayQuery(QfPayDTO qfPayDTO) {
        log.info("==================【QfPay查询接口】==================【请求参数】 qfPayDTO: {}", JSON.toJSONString(qfPayDTO));
        BaseResponse baseResponse = new BaseResponse();
        QfPayQueryDTO qfPayQueryDTO = qfPayDTO.getQfPayQueryDTO();
        String sign = addSign(ReflexClazzUtils.getFieldForStringValue(qfPayQueryDTO
        ), qfPayDTO.getChannel().getMd5KeyStr());
        Map<String, Object> map = new HashMap<>();
        map.put("x-sign", sign);
        map.put("x-appid", qfPayDTO.getChannel().getChannelMerchantId().split("\\|")[1]);
        log.info("----------------- QfPay查询接口----------------- url:{}; qfPayQueryDTO:{};headMap:{}", channelsConfig.getQfPayQueryUrl(), JSON.toJSONString(qfPayQueryDTO), JSON.toJSONString(map));
        HttpResponse httpResponse = HttpClientUtils.reqPost(channelsConfig.getQfPayQueryUrl(), qfPayQueryDTO, map);
        log.info("----------------- QfPay查询接口----------------- httpResponse:{}", JSON.toJSONString(httpResponse));
        if (httpResponse.getHttpStatus() == 200) {
            QfResDataDTO qfResDataDTO = JSON.parseObject(String.valueOf(httpResponse.getJsonObject()), QfResDataDTO.class);
            QfResDTO qfResDTO = qfResDataDTO.getData();
            if (200 == qfResDataDTO.getCode()) {
                baseResponse.setCode(qfResDataDTO.getCode().toString());
                baseResponse.setData(qfResDTO);
            } else {
                baseResponse.setCode("302");
                baseResponse.setData(qfResDTO);
            }
        } else {
            baseResponse.setCode("302");
            baseResponse.setMsg(httpResponse.getJsonObject().toJSONString());
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/2/11
     * @Descripate 退款
     **/
    @Override
    public BaseResponse qfPayRefund(QfPayDTO qfPayDTO) {
        log.info("==================【QfPay退款接口】==================【请求参数】 qfPayDTO: {}", JSON.toJSONString(qfPayDTO));
        BaseResponse baseResponse = new BaseResponse();
        QfPayRefundDTO qfPayRefundDTO = qfPayDTO.getQfPayRefundDTO();
        String sign = addSign(ReflexClazzUtils.getFieldForStringValue(qfPayRefundDTO
        ), qfPayDTO.getChannel().getMd5KeyStr());
        Map<String, Object> map = new HashMap<>();
        map.put("x-sign", sign);
        map.put("x-appid", qfPayDTO.getChannel().getChannelMerchantId().split("\\|")[1]);
        log.info("----------------- qfPay退款接口----------------- url:{};qfPayRefundDTO:{};headMap:{}", channelsConfig.getQfPayrRefundUrl(), JSON.toJSONString(qfPayRefundDTO), JSON.toJSONString(map));
        HttpResponse httpResponse = HttpClientUtils.reqPost(channelsConfig.getQfPayrRefundUrl(), qfPayRefundDTO, map);
        log.info("----------------- qfPay退款接口----------------- httpResponse:{}", JSON.toJSONString(httpResponse));
        if (httpResponse.getHttpStatus() == 200) {
            QfResDataDTO qfResDataDTO = JSON.parseObject(String.valueOf(httpResponse.getJsonObject()), QfResDataDTO.class);
            QfResDTO qfResDTO = qfResDataDTO.getData();
            if (200 == qfResDataDTO.getCode()) {
                baseResponse.setCode(qfResDataDTO.getCode().toString());
                baseResponse.setData(qfResDTO);
            } else {
                baseResponse.setCode("302");
                baseResponse.setData(qfResDTO);
            }
        } else {
            baseResponse.setCode("302");
            baseResponse.setMsg(httpResponse.getJsonObject().toJSONString());
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/2/18
     * @Descripate qfPayRefundSearch
     **/
    @Override
    public BaseResponse qfPayRefundSearch(QfPayDTO qfPayDTO) {
        log.info("==================【QfPay退款查询接口】==================【请求参数】 qfPayDTO: {}", JSON.toJSONString(qfPayDTO));
        BaseResponse baseResponse = new BaseResponse();
        QfPayQueryDTO qfPayQueryDTO = qfPayDTO.getQfPayQueryDTO();
        String sign = addSign(ReflexClazzUtils.getFieldForStringValue(qfPayQueryDTO
        ), qfPayDTO.getChannel().getMd5KeyStr());
        Map<String, Object> map = new HashMap<>();
        map.put("x-sign", sign);
        map.put("x-appid",qfPayDTO.getChannel().getChannelMerchantId().split("\\|")[1]);
        log.info("----------------- QfPay退款查询接口----------------- url:{};qfPayQueryDTO:{};headMap:{}", channelsConfig.getQfrRefundSearchUrl(), JSON.toJSONString(qfPayQueryDTO), JSON.toJSONString(map));
        HttpResponse httpResponse = HttpClientUtils.reqPost(channelsConfig.getQfrRefundSearchUrl(), qfPayQueryDTO, map);
        log.info("----------------- QfPay退款查询接口----------------- httpResponse:{}", JSON.toJSONString(httpResponse));
        if (httpResponse.getHttpStatus() == 200) {
            QfPayRefundSerDTO qfPayRefundSerDTO = JSON.parseObject(String.valueOf(httpResponse.getJsonObject()), QfPayRefundSerDTO.class);
            if (200 == qfPayRefundSerDTO.getCode()) {
                baseResponse.setCode(qfPayRefundSerDTO.getCode().toString());
                baseResponse.setData(qfPayRefundSerDTO);
            } else {
                baseResponse.setCode("302");
                baseResponse.setData(qfPayRefundSerDTO);
            }
        } else {
            baseResponse.setCode("302");
            baseResponse.setMsg(httpResponse.getJsonObject().toJSONString());
        }
        return baseResponse;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2020/2/11
     * @Descripate 验签方法
     **/
    private boolean checkSign(Map<String, String> map, String sign, String secret) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i).toString();
            String value = map.get(key).toString();
            if (StringUtil.isBlank(value)) {
                continue;
            }
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }
        content.append(secret);
        String signSrc = content.toString();
        if (signSrc.startsWith("&")) {
            signSrc.replaceFirst("&", "");
        }
        log.info("------- QfPay验签 --------- signSrc ：{}", signSrc);
        String newSign = MD5.MD5Encode(signSrc).toUpperCase();
        log.info("------- QfPay验签 --------- newSign ：{}", newSign);
        log.info("------- QfPay验签 --------- sign ：{}", sign);
        if (newSign.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/2/11
     * @Descripate 加签方法
     **/
    private String addSign(Map<String, String> map, String secret) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            log.info("------- QfPay加签 --------- key ：{}", key);
            String value = map.get(key);
            log.info("------- QfPay加签 --------- value ：{}", value);
            if (value == null || value.equals("null") || value == "") {
                continue;
            }
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }
        content.append(secret);
        String signSrc = content.toString();
        if (signSrc.startsWith("&")) {
            signSrc.replaceFirst("&", "");
        }
        log.info("------- QfPay加签 --------- signSrc ：{}", signSrc);
        String newSign = MD5.MD5Encode(signSrc).toUpperCase();
        log.info("------- QfPay加签 --------- newSign ：{}", newSign);
        return newSign;
    }
}
