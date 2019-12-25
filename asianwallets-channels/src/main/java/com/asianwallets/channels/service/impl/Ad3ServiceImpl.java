package com.asianwallets.channels.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.Ad3Service;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.ad3.*;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.HttpResponse;
import com.asianwallets.common.utils.*;
import com.asianwallets.common.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class Ad3ServiceImpl implements Ad3Service {

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private RedisService redisService;

    private String createSign(Object commonObj, Object businessObj, String token) {
        Map<String, String> commonMap = ReflexClazzUtils.getFieldForStringValue(commonObj);
        Map<String, String> businessMap = ReflexClazzUtils.getFieldForStringValue(businessObj);
        commonMap.putAll(businessMap);
        String signature = SignTools.getSignStr(commonMap);
        String clearText = signature + "&" + token;
        log.info("=================【AD3线下CSB】=================【签名前的明文】 clearText: {}", clearText);
        //与token进行拼接MD5加密
        String sign = MD5Util.getMD5String(clearText);
        log.info("=================【AD3线下CSB】=================【签名后的密文】 sign: {}", sign);
        return sign;
    }

    /**
     * AD3登陆
     *
     * @param channel 通道
     * @return AD3登陆响应参数
     */
    private AD3LoginVO offlineLogin(Channel channel) {
        AD3LoginVO ad3LoginVO = null;
        String token = redisService.get(AD3Constant.AD3_LOGIN_TOKEN);
        String terminalId = redisService.get(AD3Constant.AD3_LOGIN_TERMINAL);
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(terminalId)) {
            //AD3登陆业务参数实体
            LoginBizContentDTO bizContent = new LoginBizContentDTO(AD3Constant.LOGIN_OUT, channel);
            //AD3登陆公共参数实体
            AD3LoginDTO ad3LoginDTO = new AD3LoginDTO(channel.getChannelMerchantId(), bizContent);
            //先登出
            HttpClientUtils.reqPost(channel.getExtend5(), ad3LoginDTO, null);
            //再登陆
            bizContent.setType(AD3Constant.LOGIN_IN);
            //Channel的extend5存储的是AD3登录接口URL
            HttpResponse httpResponse = HttpClientUtils.reqPost(channel.getExtend5(), ad3LoginDTO, null);
            //状态码为200
            if (httpResponse.getHttpStatus().equals(AsianWalletConstant.HTTP_SUCCESS_STATUS)) {
                ad3LoginVO = JSON.parseObject(String.valueOf(httpResponse.getJsonObject()), AD3LoginVO.class);
                //业务返回码为成功
                if (ad3LoginVO != null && ad3LoginVO.getRespCode().equals(AD3Constant.AD3_OFFLINE_SUCCESS)) {
                    redisService.set(AD3Constant.AD3_LOGIN_TOKEN, ad3LoginVO.getToken());
                    redisService.set(AD3Constant.AD3_LOGIN_TERMINAL, ad3LoginVO.getTerminalId());
                }
            }
        } else {
            ad3LoginVO = new AD3LoginVO();
            //终端编号
            ad3LoginVO.setTerminalId(terminalId);
            //Token
            ad3LoginVO.setToken(token);
        }
        return ad3LoginVO;
    }

    /**
     * AD3线下CSB
     *
     * @param ad3CSBScanPayDTO AD3线下CSB输入实体
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineCsb(AD3CSBScanPayDTO ad3CSBScanPayDTO) {
        log.info("=================【AD3线下CSB】=================【请求参数】 ad3CSBScanPayDTO: {}", JSON.toJSONString(ad3CSBScanPayDTO));
        BaseResponse baseResponse = new BaseResponse();
        try {
            Orders orders = ad3CSBScanPayDTO.getOrders();
            ChannelsOrder channelsOrder = new ChannelsOrder();
            channelsOrder.setId(ad3CSBScanPayDTO.getBizContent().getMerOrderNo());
            channelsOrder.setMerchantOrderId(orders.getMerchantOrderId());
            channelsOrder.setTradeCurrency(orders.getTradeCurrency());
            channelsOrder.setTradeAmount(new BigDecimal(ad3CSBScanPayDTO.getBizContent().getMerorderAmount()));
            channelsOrder.setReqIp(orders.getReqIp());
            channelsOrder.setServerUrl(ad3CSBScanPayDTO.getBizContent().getReceiveUrl());
            channelsOrder.setTradeStatus(TradeConstant.TRADE_WAIT);
            channelsOrder.setIssuerId(ad3CSBScanPayDTO.getBizContent().getIssuerId());
            channelsOrder.setOrderType(AD3Constant.TRADE_ORDER);
            channelsOrder.setMd5KeyStr(ad3CSBScanPayDTO.getChannel().getMd5KeyStr());
            channelsOrder.setPayerPhone(orders.getPayerPhone());
            channelsOrder.setPayerName(orders.getPayerName());
            channelsOrder.setPayerBank(orders.getPayerBank());
            channelsOrder.setPayerEmail(orders.getPayerEmail());
            channelsOrder.setCreateTime(new Date());
            channelsOrder.setCreator(orders.getCreator());
            channelsOrderMapper.insert(channelsOrder);
            //获取AD3的终端号和Token
            AD3LoginVO ad3LoginVO = offlineLogin(ad3CSBScanPayDTO.getChannel());
            if (ad3LoginVO == null || StringUtils.isEmpty(ad3LoginVO.getToken())) {
                log.info("=================【AD3线下CSB】=================【AD3登陆接口异常】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            String payUrl = ad3CSBScanPayDTO.getChannel().getPayUrl();
            ad3CSBScanPayDTO.getBizContent().setTerminalId(ad3LoginVO.getTerminalId());
            CSBScanBizContentDTO bizContent = ad3CSBScanPayDTO.getBizContent();
            ad3CSBScanPayDTO.setBizContent(null);
            ad3CSBScanPayDTO.setOrders(null);
            ad3CSBScanPayDTO.setChannel(null);
            //生成签名
            String sign = createSign(ad3CSBScanPayDTO, bizContent, ad3LoginVO.getToken());
            ad3CSBScanPayDTO.setSignMsg(sign);
            ad3CSBScanPayDTO.setBizContent(bizContent);
            HttpResponse httpResponse = HttpClientUtils.reqPost(payUrl, ad3CSBScanPayDTO, null);
            if (httpResponse.getJsonObject() == null || !httpResponse.getHttpStatus().equals(AsianWalletConstant.HTTP_SUCCESS_STATUS)) {
                log.info("=================【AD3线下CSB】=================【接口响应结果错误】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            AD3CSBScanVO csbScanVO = httpResponse.getJsonObject().toJavaObject(AD3CSBScanVO.class);
            log.info("=================【AD3线下CSB】=================【接口响应参数】 csbScanVO: {}", JSON.toJSONString(csbScanVO));
            if (csbScanVO == null || !AD3Constant.AD3_OFFLINE_SUCCESS.equals(csbScanVO.getRespCode())) {
                log.info("=================【AD3线下CSB】=================【业务响应结果错误】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(csbScanVO.getCode_url());
        } catch (Exception e) {
            log.info("=================【AD3线下CSB】=================【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * AD3线下BSC
     *
     * @param ad3BSCScanPayDTO AD3线下BSC输入实体
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineBsc(AD3BSCScanPayDTO ad3BSCScanPayDTO) {
        log.info("=================【AD3线下BSC】=================【请求参数】 ad3BSCScanPayDTO: {}", JSON.toJSONString(ad3BSCScanPayDTO));
        BaseResponse baseResponse = new BaseResponse();
        try {
            Orders orders = ad3BSCScanPayDTO.getOrders();
            ChannelsOrder channelsOrder = new ChannelsOrder();
            channelsOrder.setId(ad3BSCScanPayDTO.getBizContent().getMerOrderNo());
            channelsOrder.setMerchantOrderId(orders.getMerchantOrderId());
            channelsOrder.setTradeCurrency(orders.getTradeCurrency());
            channelsOrder.setTradeAmount(new BigDecimal(ad3BSCScanPayDTO.getBizContent().getMerorderAmount()));
            channelsOrder.setReqIp(orders.getReqIp());
            channelsOrder.setServerUrl(ad3BSCScanPayDTO.getBizContent().getReceiveUrl());
            channelsOrder.setTradeStatus(TradeConstant.TRADE_WAIT);
            channelsOrder.setIssuerId(ad3BSCScanPayDTO.getBizContent().getIssuerId());
            channelsOrder.setOrderType(AD3Constant.TRADE_ORDER);
            channelsOrder.setMd5KeyStr(ad3BSCScanPayDTO.getChannel().getMd5KeyStr());
            channelsOrder.setPayerPhone(orders.getPayerPhone());
            channelsOrder.setPayerName(orders.getPayerName());
            channelsOrder.setPayerBank(orders.getPayerBank());
            channelsOrder.setPayerEmail(orders.getPayerEmail());
            channelsOrder.setCreateTime(new Date());
            channelsOrder.setCreator(orders.getCreator());
            channelsOrderMapper.insert(channelsOrder);
            //获取AD3的终端号和Token
            AD3LoginVO ad3LoginVO = offlineLogin(ad3BSCScanPayDTO.getChannel());
            if (ad3LoginVO == null || StringUtils.isEmpty(ad3LoginVO.getToken())) {
                log.info("=================【AD3线下BSC】=================【AD3登陆接口异常】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            String payUrl = ad3BSCScanPayDTO.getChannel().getPayUrl();
            ad3BSCScanPayDTO.getBizContent().setTerminalId(ad3LoginVO.getTerminalId());
            BSCScanBizContentDTO bizContent = ad3BSCScanPayDTO.getBizContent();
            ad3BSCScanPayDTO.setBizContent(null);
            ad3BSCScanPayDTO.setOrders(null);
            ad3BSCScanPayDTO.setChannel(null);
            //生成签名
            String sign = createSign(ad3BSCScanPayDTO, bizContent, ad3LoginVO.getToken());
            ad3BSCScanPayDTO.setSignMsg(sign);
            ad3BSCScanPayDTO.setBizContent(bizContent);
            HttpResponse httpResponse = HttpClientUtils.reqPost(payUrl, ad3BSCScanPayDTO, null);
            if (httpResponse.getJsonObject() == null || !httpResponse.getHttpStatus().equals(AsianWalletConstant.HTTP_SUCCESS_STATUS)) {
                log.info("=================【AD3线下BSC】=================【接口响应结果错误】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            AD3BSCScanVO ad3BSCScanVO = httpResponse.getJsonObject().toJavaObject(AD3BSCScanVO.class);
            log.info("=================【AD3线下BSC】=================【接口响应参数】 ad3BSCScanVO: {}", JSON.toJSONString(ad3BSCScanVO));
            if (ad3BSCScanVO == null) {
                log.info("=================【AD3线下BSC】=================【业务响应结果错误】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
            baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
            baseResponse.setData(ad3BSCScanVO);
        } catch (Exception e) {
            log.info("=================【AD3线下BSC】=================【接口异常】", e);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/20
     * @Descripate AD3线下退款接口
     **/
    @Override
    public BaseResponse offlineRefund(AD3ONOFFRefundDTO ad3ONOFFRefundDTO) {
        BaseResponse baseResponse = new BaseResponse();
        AD3RefundDTO ad3RefundDTO = ad3ONOFFRefundDTO.getAd3RefundDTO();
        Channel channel = ad3ONOFFRefundDTO.getChannel();
        log.info("===========================【AD3线下退款接口】开始时间 =========================== ad3RefundDTO :{}", JSON.toJSONString(ad3RefundDTO));
        HttpResponse httpResponse = HttpClientUtils.reqPost(channel.getRefundUrl() + "/posRefund.json", ad3RefundDTO, null);
        log.info("===========================【AD3线下退款接口】结束时间 =========================== httpResponse:{}", JSON.toJSONString(httpResponse));
        if (httpResponse.getHttpStatus() == AsianWalletConstant.HTTP_SUCCESS_STATUS) {
            AD3RefundOrderVO ad3RefundOrderVO = JSON.parseObject(String.valueOf(httpResponse.getJsonObject()), AD3RefundOrderVO.class);
            if (ad3RefundOrderVO.getRespCode() != null && ad3RefundOrderVO.getRespCode().equals(AD3Constant.AD3_OFFLINE_SUCCESS)) {
                baseResponse.setCode(String.valueOf(AsianWalletConstant.HTTP_SUCCESS_STATUS));
                baseResponse.setMsg(AD3Constant.AD3_ONLINE_SUCCESS);
                baseResponse.setData(ad3RefundOrderVO);
            } else {
                baseResponse.setCode(String.valueOf(AsianWalletConstant.HTTP_SUCCESS_STATUS));
                baseResponse.setMsg("T001");
                baseResponse.setData(ad3RefundOrderVO);
            }
        } else {
            baseResponse.setCode(String.valueOf(302));
            baseResponse.setData(null);
        }
        return baseResponse;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/20
     * @Descripate AD3线上退款接口
     **/
    @Override
    public BaseResponse onlineRefund(AD3ONOFFRefundDTO ad3ONOFFRefundDTO) {
        BaseResponse baseResponse = new BaseResponse();
        Channel channel = ad3ONOFFRefundDTO.getChannel();
        SendAdRefundDTO sendAdRefundDTO = ad3ONOFFRefundDTO.getSendAdRefundDTO();
        log.info("===========================【AD3线上退款接口】开始时间 =========================== sendAdRefundDTO :{}", JSON.toJSONString(sendAdRefundDTO));
        HttpResponse httpResponse = HttpClientUtils.reqPost(channel.getRefundUrl(), sendAdRefundDTO, null);
        log.info("===========================【AD3线上退款接口】结束时间 =========================== httpResponse:{}", JSON.toJSONString(httpResponse));
        if (httpResponse.getHttpStatus() == AsianWalletConstant.HTTP_SUCCESS_STATUS) {
            //请求成功
            RefundAdResponseVO refundAdResponseVO = JSONObject.parseObject(httpResponse.getJsonObject().toJSONString(), RefundAdResponseVO.class);
            if (refundAdResponseVO != null && refundAdResponseVO.getStatus().equals("1")) {
                baseResponse.setCode(String.valueOf(AsianWalletConstant.HTTP_SUCCESS_STATUS));
                baseResponse.setMsg(AD3Constant.AD3_ONLINE_SUCCESS);
                baseResponse.setData(refundAdResponseVO);
            } else {
                baseResponse.setCode(String.valueOf(AsianWalletConstant.HTTP_SUCCESS_STATUS));
                baseResponse.setMsg("T001");
                baseResponse.setData(refundAdResponseVO);
            }
        } else {
            baseResponse.setCode(String.valueOf(302));
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    /**
     * AD3 线上收款
     *
     * @param ad3OnlineAcquireDTO AD3线上收单接口参数实体
     * @return BaseResponse
     */
    @Override
    public BaseResponse onlinePay(AD3OnlineAcquireDTO ad3OnlineAcquireDTO) {
        BaseResponse response = new BaseResponse();
        cn.hutool.http.HttpResponse execute = HttpRequest.post(ad3OnlineAcquireDTO.getUrl())
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(BeanToMapUtil.beanToMap(ad3OnlineAcquireDTO))
                .timeout(10000)
                .execute();
        int status = execute.getStatus();
        String body = execute.body();
        log.info("----------------------向上游接口发送订单接口返回----------------------http状态码:{},body:{}", status, JSON.toJSON(body));
        //判断HTTP状态码
        if (status != AsianWalletConstant.HTTP_SUCCESS_STATUS || StringUtils.isEmpty(body)) {
            response.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
            return response;
        }
        response.setData(body);
        return response;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/24
     * @Descripate AD3查询接口
     **/
    @Override
    public BaseResponse query(AD3ONOFFRefundDTO ad3ONOFFRefundDTO) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode("T001");
        log.info("===========================【AD3查询接口】开始时间 =========================== ad3ONOFFRefundDTO :{}", JSON.toJSONString(ad3ONOFFRefundDTO));
        HttpResponse httpResponse = HttpClientUtils.reqPost(ad3ONOFFRefundDTO.getChannel().getChannelSingleSelectUrl(), ad3ONOFFRefundDTO.getAd3QuerySingleOrderDTO(), null);
        log.info("===========================【AD3线上退款接口】结束时间 =========================== httpResponse:{}", JSON.toJSONString(httpResponse));
        if (httpResponse == null || !httpResponse.getHttpStatus().equals(AsianWalletConstant.HTTP_SUCCESS_STATUS)) {
            //请求失败
            baseResponse.setCode("T001");
            return baseResponse;
        }
        //反序列化Json数据
        AD3OrdersVO ad3OrdersVO = JSON.parseObject(String.valueOf(httpResponse.getJsonObject()), AD3OrdersVO.class);
        if (ad3OrdersVO == null || !ad3OrdersVO.getRespCode().equals(AD3Constant.AD3_OFFLINE_SUCCESS)) {
            baseResponse.setCode("T001");
            return baseResponse;
        }
        if (ad3OrdersVO == null || !ad3OrdersVO.getRespCode().equals(AD3Constant.AD3_OFFLINE_SUCCESS)) {
            //业务失败
            baseResponse.setCode("T001");
            return baseResponse;
        } else {
            //查询成功
            baseResponse.setCode("T000");
            baseResponse.setData(ad3OrdersVO);
        }
        return baseResponse;
    }
}
