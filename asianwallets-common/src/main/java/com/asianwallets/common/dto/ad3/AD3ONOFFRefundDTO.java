package com.asianwallets.common.dto.ad3;

import com.asianwallets.common.entity.Channel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-20 17:42
 **/
@Data
@ApiModel(value = "AD3退款接口实体", description = "AD3线下退款接口实体")
public class AD3ONOFFRefundDTO {

    @ApiModelProperty(value = "通道")
    private Channel channel;

    @ApiModelProperty(value = "AD3线下退款接口实体")
    private AD3RefundDTO ad3RefundDTO;

    @ApiModelProperty(value = "Ad3退款上报实体")
    private SendAdRefundDTO sendAdRefundDTO;

    @ApiModelProperty(value = "Ad3查询实体")
    private AD3QuerySingleOrderDTO ad3QuerySingleOrderDTO;

    public AD3ONOFFRefundDTO() {
    }
}
