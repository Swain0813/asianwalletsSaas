package com.asianwallets.trade.channels.th.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.entity.Channel;
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
        //交易金额
        iso8583DTO.setAmountOfTransactions_4("");
        iso8583DTO.setAmountOfTips_5("");
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
        iso8583DTO.setCurrencyCodeOfTransaction_49("");
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
}
