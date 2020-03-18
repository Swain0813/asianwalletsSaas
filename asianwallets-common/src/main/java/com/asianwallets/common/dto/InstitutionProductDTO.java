package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel(value = "机构产品查询输入参数", description = "机构产品查询输入参数")
public class InstitutionProductDTO extends BasePageHelper {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "交易类型")//1-线上 2-线下
    private String transType;

    @ApiModelProperty(value = "支付方式")
    private String payType;


    @ApiModelProperty(value = "语言")
    private String language;

}
