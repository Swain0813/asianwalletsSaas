package com.asianwallets.trade.channels.upi;

import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;

import java.util.Map;

public interface Upiservice {


    String upiServerCallback(JSONObject jsonObject);
}
