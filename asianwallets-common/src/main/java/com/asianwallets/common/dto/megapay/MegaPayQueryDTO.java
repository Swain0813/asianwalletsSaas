package com.asianwallets.common.dto.megapay;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: XuWenQi
 * @create: 2019-11-04 15:08
 **/
@Data
@ApiModel(value = "MegaPay通道请求实体", description = "MegaPay通道请求实体")
public class MegaPayQueryDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单id")
    private String invoice;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户id")
    private String merchantID;

    public MegaPayQueryDTO() {
    }

    public MegaPayQueryDTO(String invoice, String merchantID) {
        this.invoice = invoice;
        this.merchantID = merchantID;
    }
}
