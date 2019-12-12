package com.asianwallets.common.dto.megapay;

import com.asianwallets.common.entity.Channel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: Nextpos查询订单请求实体
 * @author: XuWenQi
 * @create: 2019-08-09 10:26
 **/
@Data
@ApiModel(value = "Nextpos查询订单请求实体", description = "Nextpos查询订单请求实体")
public class NextPosQueryDTO {

    @ApiModelProperty(value = "商户id")
    private String merID;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "merRespPassword")
    private String merRespPassword;

    @ApiModelProperty(value = "merRespID")
    private String merRespID;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public NextPosQueryDTO() {
    }

    public NextPosQueryDTO(String merID, String orderId) {
        this.merID = merID;
        this.orderId = orderId;
    }

    public NextPosQueryDTO(String orderId, Channel channel) {
        this.merID = channel.getChannelMerchantId();
        this.orderId = orderId;
        this.merRespPassword = channel.getMd5KeyStr();
        this.merRespID = channel.getPayCode();
        this.channel = channel;
    }
}
