package com.asianwallets.common.dto.upi;

import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.entity.Channel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-03 14:43
 **/
@Data
@ApiModel(value = "UpiDTO", description = "UpiDTO")
public class UpiDTO {

    public Channel channel;

    public UpiPayDTO upiPayDTO;

    public UpiRefundDTO upiRefundDTO;

    public UpiDownDTO upiDownDTO;

    public String iso8583DTO;




}
