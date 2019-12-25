package com.asianwallets.trade.feign.impl;

import com.asianwallets.common.dto.ad3.AD3BSCScanPayDTO;
import com.asianwallets.common.dto.ad3.AD3CSBScanPayDTO;
import com.asianwallets.common.dto.ad3.AD3ONOFFRefundDTO;
import com.asianwallets.common.dto.ad3.AD3OnlineAcquireDTO;
import com.asianwallets.common.dto.alipay.*;
import com.asianwallets.common.dto.doku.DOKUReqDTO;
import com.asianwallets.common.dto.eghl.EGHLRequestDTO;
import com.asianwallets.common.dto.enets.EnetsBankRequestDTO;
import com.asianwallets.common.dto.enets.EnetsOffLineRequestDTO;
import com.asianwallets.common.dto.help2pay.Help2PayOutDTO;
import com.asianwallets.common.dto.help2pay.Help2PayRequestDTO;
import com.asianwallets.common.dto.megapay.*;
import com.asianwallets.common.dto.nganluong.NganLuongDTO;
import com.asianwallets.common.dto.vtc.VTCRequestDTO;
import com.asianwallets.common.dto.wechat.*;
import com.asianwallets.common.dto.xendit.XenditDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.trade.feign.ChannelsFeign;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;


@Component
public class ChannelsFeignImpl implements ChannelsFeign {

    @Override
    public BaseResponse eGHLPay(EGHLRequestDTO eghlRequestDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse megaPayTHB(MegaPayRequestDTO megaPayRequestDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse megaPayIDR(MegaPayIDRRequestDTO megaPayIDRRequestDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse vtcPay(VTCRequestDTO vtcRequestDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse eNetsBankPay(EnetsBankRequestDTO enetsBankRequestDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse help2Pay(Help2PayRequestDTO help2PayRequestDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse help2PayOut(Help2PayOutDTO help2PayOutDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse nextPosCsb(NextPosRequestDTO nextPosRequestDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse eNetsPosCSBPay(EnetsOffLineRequestDTO enetsOffLineRequestDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse aliPayOfflineBSC(AliPayOfflineBSCDTO aliPayOfflineBSCDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse aliPayCSB(AliPayCSBDTO aliPayCSBDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse nganLuongPay(NganLuongDTO nganLuongDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse xenditPay(XenditDTO xenditDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse alipayRefund(AliPayRefundDTO aliPayRefundDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse wechatOfflineBSC(WechatBSCDTO wechatBSCDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse wechatOfflineCSB(WechatCSBDTO wechatCSBDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse wechatRefund(WechaRefundDTO wechaRefundDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse alipayQuery(AliPayQueryDTO aliPayQueryDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse wechatQuery(WechatQueryDTO wechatQueryDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse alipayCancel(AliPayCancelDTO aliPayCancelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse wechatCancel(WechatCancelDTO wechatCancelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse nextPosRefund(NextPosRefundDTO nextPosRefundDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse nextPosQuery(NextPosQueryDTO nextPosQueryDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse dokuPay(DOKUReqDTO dokuReqDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse megaPayQuery(MegaPayQueryDTO megaPayQueryDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse ad3OfflineCsb(AD3CSBScanPayDTO ad3CSBScanPayDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse ad3OfflineBsc(AD3BSCScanPayDTO ad3CSBScanPayDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse ad3OnlinePay(AD3OnlineAcquireDTO ad3OnlineAcquireDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse ad3OfflineRefund(AD3ONOFFRefundDTO ad3RefundDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse ad3OnlineRefund(AD3ONOFFRefundDTO sendAdRefundDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse query(@RequestBody @ApiParam AD3ONOFFRefundDTO ad3ONOFFRefundDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
