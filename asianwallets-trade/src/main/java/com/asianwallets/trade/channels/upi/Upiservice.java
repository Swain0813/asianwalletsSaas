package com.asianwallets.trade.channels.upi;
import com.alibaba.fastjson.JSONObject;
public interface Upiservice {


    String upiServerCallback(JSONObject jsonObject);
}
