package com.asianwallets.common.dto.nganluong;

import com.asianwallets.common.entity.Channel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: NGANLUONG通道查询请求实体
 * @author: XuWenQi
 * @create: 2019-10-30 14:33
 **/
@Data
@ApiModel(value = "NGANLUONG通道查询请求实体", description = "NGANLUONG通道查询请求实体")
public class NganLuongQueryDTO {

    @ApiModelProperty(value = "商户id")
    private String merchant_id;

    @ApiModelProperty(value = "商户密码")
    private String merchant_password;

    @ApiModelProperty(value = "版本号")
    private String version = "3.1";

    @ApiModelProperty(value = "SetExpressCheckout")
    private String function;

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "Channel")
    private Channel channel;


    public NganLuongQueryDTO() {
    }

    public NganLuongQueryDTO(Channel channel) {
        this.channel = channel;
    }
}
