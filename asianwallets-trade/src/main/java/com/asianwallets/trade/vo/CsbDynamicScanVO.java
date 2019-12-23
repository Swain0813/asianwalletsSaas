package com.asianwallets.trade.vo;

import com.asianwallets.common.constant.TradeConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "线下同机构CSB动态扫码输出实体", description = "线下同机构CSB动态扫码输出实体")
public class CsbDynamicScanVO {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "二维码URL")
    private String qrCodeUrl;

    @ApiModelProperty(value = "解码类型")
    private String decodeType = TradeConstant.NO_DECODE;

}
