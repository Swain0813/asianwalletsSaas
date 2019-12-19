package com.asianwallets.trade.channels.ad3;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;

public interface Ad3Service {

    BaseResponse offlineCSB(Orders orders, Channel channel);
}
