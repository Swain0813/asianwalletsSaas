package com.asianwallets.trade.channels.upi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.upi.UpiDTO;
import com.asianwallets.common.dto.upi.UpiPayDTO;
import com.asianwallets.common.dto.upi.utils.CryptoUtil;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.upi.Upiservice;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-04 14:40
 **/
@Slf4j
@Service
@HandlerType(TradeConstant.UPI)
public class UpiserviceImpl extends ChannelsAbstractAdapter implements Upiservice {

    @Autowired
    private ChannelsFeign channelsFeign;
    @Autowired
    @Qualifier(value = "ad3ParamsConfig")
    private AD3ParamsConfig ad3ParamsConfig;

    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {

        UpiDTO upiDTO = new UpiDTO();
        upiDTO.setChannel(channel);
        UpiPayDTO upiPayDTO = this.createCSBDTO(orders,channel);
        upiDTO.setUpiPayDTO(upiPayDTO);
        log.info("==================【UPI线下CSB】==================【调用Channels服务】【QfPay-CSB接口】  upiDTO: {}", JSON.toJSONString(upiDTO));
        BaseResponse channelResponse = channelsFeign.upiPay(upiDTO);
        log.info("==================【UPI线下CSB】==================【调用Channels服务】【QfPay-CSB接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【UPI线下CSB】==================【调用Channels服务】【QfPay-CSB接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(channelResponse.getData().toString());
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(jsonObject.get("qrCode"));
        return baseResponse;
    }

    /**
     * @Author YangXu
     * @Date 2020/6/4
     * @Descripate 创建CSBDTO
     * @return
     **/
    private UpiPayDTO createCSBDTO(Orders orders, Channel channel) {
        UpiPayDTO upiPayDTO = new UpiPayDTO();
        upiPayDTO.setVersion("2.0.0");
        upiPayDTO.setTrade_code("PAY");
        // BACKSTAGEALIPAY 银行直连参数 UNIONZS：银联国际二维码主扫，BACKSTAGEUNION：银联国际二维码反扫
        //主扫CSB 反扫BSC
        upiPayDTO.setBank_code("UNIONZS");
        upiPayDTO.setAgencyId(channel.getChannelMerchantId());
        upiPayDTO.setTerminal_no(channel.getExtend1());
        upiPayDTO.setOrder_no(orders.getId());
        upiPayDTO.setAmount(orders.getTradeAmount().toString());
        upiPayDTO.setCurrency_type(orders.getTradeCurrency());
        upiPayDTO.setSett_currency_type(orders.getTradeCurrency());
        upiPayDTO.setProduct_name(channel.getExtend6());
        upiPayDTO.setReturn_url(ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/upiServerCallback"));
        upiPayDTO.setNotify_url(ad3ParamsConfig.getChannelCallbackUrl().concat("/offlineCallback/upiServerCallback"));
        upiPayDTO.setClient_ip(orders.getReqIp());
        return upiPayDTO;
    }

    @Override
    public String upiServerCallback(JSONObject jsonObject) {
        //final PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix(upiDTO.getChannel().getExtend5(), "pem", "RSA");
        //final PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix(upiDTO.getChannel().getMd5KeyStr(), "pem", null, "RSA");



    return "success";
    }
}
