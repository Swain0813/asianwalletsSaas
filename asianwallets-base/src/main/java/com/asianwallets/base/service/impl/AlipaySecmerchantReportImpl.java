package com.asianwallets.base.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.ChannelMapper;
import com.asianwallets.base.dao.MerchantMapper;
import com.asianwallets.base.dao.MerchantReportMapper;
import com.asianwallets.base.feign.MessageFeign;
import com.asianwallets.base.rabbitmq.RabbitMQSender;
import com.asianwallets.base.service.AlipaySecmerchantReport;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.MerchantReport;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.utils.*;
import com.asianwallets.common.vo.ChannelDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
支付宝二级商户报备
 */
@Async
@Component
@Slf4j
public class AlipaySecmerchantReportImpl implements AlipaySecmerchantReport {

    @Autowired
    private MerchantReportMapper merchantReportMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private MessageFeign messageFeign;

    @Value("${custom.warning.mobile}")
    private String mobile;

    @Value("${custom.warning.email}")
    private String email;

    @Value("${custom.alipay.url}")
    private String url;

    @Autowired
    private RedisService redisService;

    /**
     * 重新报备
     *
     * @param mrId
     */
    public void Resubmit(String mrId) {
        MerchantReport merchantReport = merchantReportMapper.selectByPrimaryKey(mrId);
        if (merchantReport.getEnabled() == null || !merchantReport.getEnabled()) {
            try {
                getHttpResponse(merchantReport);
            } catch (IOException e) {
                log.warn("------------------报备异常------------------信息:{}", JSON.toJSONString(e));
                messageFeign.sendSimple(mobile, "SAAS-支付宝报备异常  ：{ " + merchantReport + " }");//短信通知
                messageFeign.sendSimpleMail(email, "SAAS-支付宝报备异常 ", "支付宝报备异常  ：{ " + merchantReport + " }");//邮件通知
            }
        }
    }

    /**
     * 报备
     *
     * @param merchantId
     * @param channelId
     * @throws IOException
     */
    public void report(String merchantId, String channelId) {
        ChannelDetailVO channelDetailVO = channelMapper.selectByChannelId(channelId, null);
        MerchantReport mr = merchantReportMapper.selectByChannelCodeAndMerchantId(channelDetailVO.getChannelCode(), merchantId);
        if (mr != null) {
            log.warn("------------------报备信息已存在------------------信息:{}", JSON.toJSONString(mr));
            return;
        }
        Merchant merchant = merchantMapper.getMerchantReportInfo(merchantId, channelDetailVO.getChannelCode());
        MerchantReport merchantReport = new MerchantReport();
        merchantReport.setId(IDS.uuid2());
        merchantReport.setMerchantId(merchantId);
        merchantReport.setMerchantName(merchant.getCnName());
        merchantReport.setCountryCode(merchant.getExt2());
        merchantReport.setInstitutionId(merchant.getInstitutionId());
        merchantReport.setInstitutionName(merchant.getExt1());
        merchantReport.setChannelCode(channelDetailVO.getChannelCode());
        merchantReport.setChannelName(channelDetailVO.getChannelCnName());
        merchantReport.setChannelMcc(merchant.getExt3());
        merchantReport.setSubMerchantCode(merchantId);
        merchantReport.setSubMerchantName(merchant.getCnName());
        merchantReport.setSiteUrl(merchant.getMerchantWebUrl());
        merchantReport.setCreateTime(new Date());
        merchantReport.setExtend1(merchant.getContactAddress());
        merchantReport.setExtend2(merchant.getCompanyRegistNumber());
        merchantReport.setExtend3(merchant.getLegalName());
        merchantReport.setExtend4(merchant.getLegalPassportCode());
        merchantReport.setExtend5(channelDetailVO.getChannelMerchantId());
        merchantReport.setExtend6(channelDetailVO.getMd5KeyStr());
        merchantReport.setShopName("");
        merchantReport.setShopCode("");
        merchantReport.setSubAppid("");
        merchantReport.setSiteType("WEB");
        merchantReport.setCreator("上报支付宝报备接口");
        merchantReportMapper.insert(merchantReport);
        try {
            HttpResponse response = getHttpResponse(merchantReport);
            //响应处理
            if (response == null) {
                //放到队列
                RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.FIVE, JSON.toJSONString(merchantReport));
                rabbitMQSender.send(AD3MQConstant.E_MQ_REPORT_FAIL, JSON.toJSONString(rabbitMassage));
                return;
            }
            String xml = response.getStringResult();
            Map<String, Object> xmlMap = XmlUtil.xmlToMap(xml);
            if (xmlMap.get("is_success").equals("T")) {
                //正确 更新数据
                merchantReport.setEnabled(true);
                merchantReport.setUpdateTime(new Date());
                int result = merchantReportMapper.updateByPrimaryKeySelective(merchantReport);
                if(result>0){
                    redisService.set(AsianWalletConstant.MERCHANT_REPORT_CACHE_KEY.concat("_").
                            concat(merchantReport.getMerchantId()).concat("_").concat(merchantReport.getChannelCode()), JSON.toJSONString(merchantReport));
                }
            } else {
                //错误 更新数据 发消息提醒错误
                String msg = (String) xmlMap.get("error");
                merchantReport.setEnabled(false);
                merchantReport.setRemark(msg);
                merchantReportMapper.updateByPrimaryKeySelective(merchantReport);
            }
        } catch (IOException e) {
            log.warn("------------------报备异常------------------信息:{}", JSON.toJSONString(e));
            messageFeign.sendSimple(mobile, "SAAS-支付宝报备异常  ：{ " + merchantReport + " }");//短信通知
            messageFeign.sendSimpleMail(email, "SAAS-支付宝报备异常 ", "支付宝报备异常  ：{ " + merchantReport + " }");//邮件通知
        }
    }

    /**
     * 请求支付宝报备接口
     *
     * @param merchantReport
     * @return HttpResponse
     * @throws IOException e
     */
    public HttpResponse getHttpResponse(MerchantReport merchantReport) throws IOException {
        Map<String, String> maps = new HashMap<>();
        /*
         * 服务名称
         */
        maps.put("service", "alipay.overseas.secmerchant.online.maintain");
        /*
         * 通道商户号
         */
        maps.put("partner", merchantReport.getExtend5());
        /*
         * 请求数据编码所使用的字符集
         */
        maps.put("_input_charset", "UTF-8");
        /*
         * 商户类型：个人企业传INDIVIDUAL、非个人企业ENTERPRISE
         */
        maps.put("secondary_merchant_type", "ENTERPRISE");
        /*
         * 系统发送请求的时间
         */
        maps.put("timestamp", DateUtil.formatDateTime(new Date()));
        /*
         * 商户进件时的商户名称
         */
        maps.put("secondary_merchant_name", merchantReport.getMerchantName());
        /*
         * 我司为商户分配的商户唯一编号
         */
        maps.put("secondary_merchant_id", merchantReport.getMerchantId());
        /*
         * 通道MCC编码
         */
        maps.put("secondary_merchant_industry", merchantReport.getChannelMcc());
        /*
         * 国家二位代码
         */
        maps.put("register_country", merchantReport.getCountryCode());
        /*
         * 商户地址
         */
        maps.put("register_address", merchantReport.getExtend1());
        /*
         * json信息
         */
        String urlInfos = "[{" +
                "\"site_type\":\"WEB\"," +
                "\"site_url\":\"" + merchantReport.getSiteUrl() + "," +
                "\"site_name\":\"websit\"" +
                "}]";
        maps.put("site_infos", urlInfos);
        /*
         * 公司注册号
         */
        maps.put("registration_no", merchantReport.getExtend2());
        /*
         * 法人姓名
         */
        maps.put("shareholder_name", merchantReport.getExtend3());
        /*
         * 法人证件编号
         */
        maps.put("shareholder_id", merchantReport.getExtend4());
        /*
         * 法人姓名
         */
        maps.put("representative_name", merchantReport.getExtend3());
        /*
         * 法人证件编号
         */
        maps.put("representative_id", merchantReport.getExtend4());
        //进行签名
        Map<String, String> processedMaps = AlipayCore.buildRequestPara(maps, merchantReport.getExtend6());
        log.info("-----------------支付宝报备 发起请求参数-----------------:{}",JSON.toJSONString(processedMaps));
        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
        HttpRequest reportMsg = new HttpRequest(HttpResultType.BYTES);
        reportMsg.setCharset("UTF-8");
        reportMsg.setParameters(AlipayCore.generatNameValuePair(processedMaps));
        reportMsg.setUrl(url + "_input_charset=UTF-8");
        //发送请求
        return httpProtocolHandler.execute(reportMsg, "", "");
    }


}
