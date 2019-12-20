package com.asianwallets.channels.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.Ad3Service;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.ad3.AD3CSBScanPayDTO;
import com.asianwallets.common.dto.ad3.AD3LoginDTO;
import com.asianwallets.common.dto.ad3.LoginBizContentDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.HttpResponse;
import com.asianwallets.common.utils.HttpClientUtils;
import com.asianwallets.common.utils.MD5Util;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.utils.SignTools;
import com.asianwallets.common.vo.AD3CSBScanVO;
import com.asianwallets.common.vo.AD3LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
     * @param ad3CSBScanPayDTO AD3线下CSB输入实体
     * @return AD3登陆响应参数
     */
    private AD3LoginVO offlineLogin(AD3CSBScanPayDTO ad3CSBScanPayDTO) {
        AD3LoginVO ad3LoginVO = null;
        String token = redisService.get(AD3Constant.AD3_LOGIN_TOKEN);
        String terminalId = redisService.get(AD3Constant.AD3_LOGIN_TERMINAL);
        Channel channel = ad3CSBScanPayDTO.getChannel();
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(terminalId)) {
            //AD3登陆业务参数实体
            LoginBizContentDTO bizContent = new LoginBizContentDTO(AD3Constant.LOGIN_OUT, channel);
            //AD3登陆公共参数实体
            AD3LoginDTO ad3LoginDTO = new AD3LoginDTO(ad3CSBScanPayDTO.getMerchantId(), bizContent);
            //先登出
            HttpClientUtils.reqPost(channel.getExtend5(), ad3LoginDTO, null);
            //再登陆
            bizContent.setType(AD3Constant.LOGIN_IN);
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
            channelsOrder.setTradeStatus(Byte.valueOf(TradeConstant.TRADE_WAIT));
            channelsOrder.setIssuerId(ad3CSBScanPayDTO.getBizContent().getIssuerId());
            channelsOrder.setOrderType(Byte.valueOf(AD3Constant.TRADE_ORDER));
            channelsOrder.setMd5KeyStr(ad3CSBScanPayDTO.getChannel().getMd5KeyStr());
            channelsOrderMapper.insert(channelsOrder);
            //获取AD3的终端号和Token
            AD3LoginVO ad3LoginVO = offlineLogin(ad3CSBScanPayDTO);
            if (ad3LoginVO == null || StringUtils.isEmpty(ad3LoginVO.getToken())) {
                log.info("=================【AD3线下CSB】=================【AD3登陆接口异常】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            String payUrl = ad3CSBScanPayDTO.getChannel().getPayUrl();
            ad3CSBScanPayDTO.setOrders(null);
            ad3CSBScanPayDTO.setChannel(null);
            //生成签名
            String sign = createSign(ad3CSBScanPayDTO, ad3CSBScanPayDTO.getBizContent(), ad3LoginVO.getToken());
            ad3CSBScanPayDTO.setSignMsg(sign);
            HttpResponse httpResponse = HttpClientUtils.reqPost(payUrl, ad3CSBScanPayDTO, null);
            if (!httpResponse.getHttpStatus().equals(AsianWalletConstant.HTTP_SUCCESS_STATUS)) {
                log.info("=================【AD3线下CSB】=================【响应状态码错误】");
                baseResponse.setCode(TradeConstant.HTTP_FAIL);
                baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return baseResponse;
            }
            AD3CSBScanVO csbScanVO = JSON.parseObject(String.valueOf(httpResponse.getJsonObject()), AD3CSBScanVO.class);
            log.info("=================【AD3线下CSB】=================【接口响应参数】 csbScanVO: {}", JSON.toJSONString(csbScanVO));
            if (csbScanVO == null || !AD3Constant.AD3_OFFLINE_SUCCESS.equals(csbScanVO.getRespCode())) {
                log.info("=================【AD3线下CSB】=================【响应业务码错误】");
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
}
