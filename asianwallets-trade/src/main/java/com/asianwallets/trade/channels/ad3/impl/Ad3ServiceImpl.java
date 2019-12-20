package com.asianwallets.trade.channels.ad3.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.ad3.AD3CSBScanPayDTO;
import com.asianwallets.common.dto.ad3.CSBScanBizContentDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@HandlerType(TradeConstant.AD3)
public class Ad3ServiceImpl extends ChannelsAbstractAdapter implements Ad3Service {

    @Autowired
    private ChannelsFeign channelsFeign;

    /**
     * AD3线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        //CSB请求二维码接口公共参数实体
        AD3CSBScanPayDTO ad3CSBScanPayDTO = new AD3CSBScanPayDTO(orders,channel);
        //CSB请求二维码接口业务参数实体
        CSBScanBizContentDTO csbScanBizContent = new CSBScanBizContentDTO(orders, channel);
        ad3CSBScanPayDTO.setBizContent(csbScanBizContent);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【AD3线下CSB接口请求参数】 ad3CSBScanPayDTO: {}", JSON.toJSONString(ad3CSBScanPayDTO));
        BaseResponse channelResponse = channelsFeign.ad3OfflineCsb(ad3CSBScanPayDTO);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【AD3线下CSB接口响应参数】 channelResponse: {}", JSON.toJSONString(channelResponse));
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(channelResponse.getData());
        return baseResponse;
    }
}
