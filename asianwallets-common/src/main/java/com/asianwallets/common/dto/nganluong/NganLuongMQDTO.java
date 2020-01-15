package com.asianwallets.common.dto.nganluong;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: NGANLUONG队列查询实体
 * @author: XuWenQi
 * @create: 2019-10-31 10:26
 **/
@Data
@ApiModel(value = "NGANLUONG队列查询实体", description = "NGANLUONG队列查询实体")
public class NganLuongMQDTO {

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "通道商户号")
    private String channelMerchantId;

    @ApiModelProperty(value = "md5Key")
    private String md5Key;

    @ApiModelProperty(value = "checkUrl")
    private String checkUrl;

    public NganLuongMQDTO() {
    }

    public NganLuongMQDTO(String token, String orderId, String channelMerchantId, String md5Key, String checkUrl) {
        this.token = token;
        this.orderId = orderId;
        this.channelMerchantId = channelMerchantId;
        this.md5Key = md5Key;
        this.checkUrl = checkUrl;
    }
}
