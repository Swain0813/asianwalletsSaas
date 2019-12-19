package com.asianwallets.trade.channels.ad3.impl;

import com.asianwallets.common.dto.ad3.AD3CSBScanPayDTO;
import com.asianwallets.common.dto.ad3.CSBScanBizContentDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import com.asianwallets.trade.feign.ChannelsFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class Ad3ServiceImpl extends ChannelsAbstractAdapter implements Ad3Service {

    @Autowired
    private ChannelsFeign channelsFeign;

    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        //CSB请求二维码接口公共参数实体
        AD3CSBScanPayDTO ad3CSBScanPayDTO = new AD3CSBScanPayDTO(channel.getChannelMerchantId());
        //CSB请求二维码接口业务参数实体
        CSBScanBizContentDTO csbScanBizContent = new CSBScanBizContentDTO(orders, channel.getExtend2(), channel.getExtend3(), channel.getNotifyServerUrl(), channel);
        //channelsFeign.ad3OfflineCsb()
        BaseResponse baseResponse = new BaseResponse();
        return null;
    }
}
