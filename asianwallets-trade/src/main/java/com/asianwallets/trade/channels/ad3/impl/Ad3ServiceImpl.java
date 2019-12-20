package com.asianwallets.trade.channels.ad3.impl;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.ad3.Ad3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class Ad3ServiceImpl extends ChannelsAbstractAdapter implements Ad3Service {

    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        BaseResponse baseResponse = new BaseResponse();
        return null;
    }
}
