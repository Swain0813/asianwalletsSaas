package com.asianwallets.trade.dto;


import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "通华查询队列实体", description = "通华查询队列实体")
public class ThCheckOrderQueueDTO {

    @ApiModelProperty(value = "订单")
    private Orders orders;

    @ApiModelProperty(value = "通道")
    private Channel channel;

    @ApiModelProperty(value = "ISO8583DTO")
    private ISO8583DTO iso8583DTO;

    public ThCheckOrderQueueDTO() {
    }

    public ThCheckOrderQueueDTO(Orders orders, Channel channel,ISO8583DTO iso8583DTO) {
        this.orders = orders;
        this.channel = channel;
        this.iso8583DTO = iso8583DTO;
    }
}
