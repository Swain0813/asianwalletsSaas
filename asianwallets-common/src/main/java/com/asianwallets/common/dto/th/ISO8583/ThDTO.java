package com.asianwallets.common.dto.th.ISO8583;

import com.asianwallets.common.entity.Channel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "通华DTO", description = "通华DTO")
public class ThDTO {

    @ApiModelProperty(value = "8583报文")
    public ISO8583DTO iso8583DTO;

    @ApiModelProperty(value = "通道")
    public Channel channel;

    @ApiModelProperty(value = "商户号")
    public String merchantId;

    public ThDTO() {
    }

    public ThDTO(ISO8583DTO iso8583DTO, Channel channel, String merchantId) {
        this.iso8583DTO = iso8583DTO;
        this.channel = channel;
        this.merchantId = merchantId;
    }
}
