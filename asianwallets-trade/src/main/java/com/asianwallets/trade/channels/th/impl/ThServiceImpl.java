package com.asianwallets.trade.channels.th.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Currency;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.th.ThService;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@HandlerType(TradeConstant.TH)
public class ThServiceImpl extends ChannelsAbstractAdapter implements ThService {

    @Autowired
    private ChannelsFeign channelsFeign;

    /**
     * 通华主扫接口
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        //交易处理码
        iso8583DTO.setProcessingCode_3("000000");
        String tradeAmountStr = String.valueOf(orders.getTradeAmount());
        int tradeAmount = 0;
        //交易金额
        if (new BigDecimal(orders.getTradeAmount().intValue()).compareTo(orders.getTradeAmount()) == 0) {
            //整数
            tradeAmount = orders.getTradeAmount().intValue();
        } else {
            //小数位数
            int numOfBits = tradeAmountStr.length() - tradeAmountStr.indexOf(".") - 1;
            //小数,扩大对应小数位数
            tradeAmount = orders.getTradeAmount().movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        iso8583DTO.setAmountOfTransactions_4(formatAmount);
        //iso8583DTO.setAmountOfTips_5("");
        iso8583DTO.setSystemTraceAuditNumber_11("");
        iso8583DTO.setTimeOfLocalTransaction_12("");
        iso8583DTO.setDateOfLocalTransaction_13("");
        iso8583DTO.setPointOfServiceEntryMode_22("");
        iso8583DTO.setPointOfServiceConditionMode_25("");
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("");
        iso8583DTO.setRetrievalReferenceNumber_37("");
        iso8583DTO.setResponseCode_39("");
        iso8583DTO.setCardAcceptorTerminalIdentification_41("");
        iso8583DTO.setCardAcceptorIdentificationCode_42("");
        //iso8583DTO.setAdditionalData_46("");
        //iso8583DTO.setAdditionalDataPrivate_47("");
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("156");
        iso8583DTO.setReservedPrivate_60("");
        iso8583DTO.setReservedPrivate_63("");
        iso8583DTO.setMessageAuthenticationCode_64("");
        log.info("==================【通华线下CSB】==================【调用Channels服务】【请求参数】 iso8583DTO: {}", JSON.toJSONString(iso8583DTO));
        BaseResponse channelResponse = channelsFeign.thCSB(iso8583DTO);
        log.info("==================【通华线下CSB】==================【调用Channels服务】【通华-CSB接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【通华线下CSB】==================【调用Channels服务】【通华-CSB接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        return baseResponse;
    }

    public static void main(String[] args) {


        Orders orders = new Orders();
        orders.setTradeAmount(new BigDecimal("1000.201"));
        Currency currency = new Currency();
        int tradeAmount = 0;
        //交易金额
        if (new BigDecimal(orders.getTradeAmount().intValue()).compareTo(orders.getTradeAmount()) == 0) {
            //整数
            tradeAmount = orders.getTradeAmount().intValue();
        } else {
            //小数
            tradeAmount = orders.getTradeAmount().movePointRight(2).intValue();
        }
        System.out.println(tradeAmount);

        String format = String.format("%012d", tradeAmount);
        System.out.println(format);

        String ssss = "12.01100";
        int bitPos = ssss.indexOf(".");
        int numOfBits = ssss.length() - bitPos - 1;
        System.out.println(numOfBits);
    }
}
