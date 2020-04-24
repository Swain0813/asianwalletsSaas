package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "银行输入实体", description = "银行输入实体")
public class BankDTO extends BasePageHelper {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "银行国家")
    private String bankCountry;

    @ApiModelProperty(value = "银行id")
    private String bankId;

    @ApiModelProperty(value = "银行机构号")
    private String issuerId;

    @ApiModelProperty(value = "银行币种")
    private String bankCurrency;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "银行编码")
    private String bankCode;

    @ApiModelProperty(value = "银行图片")
    private String bankImg;
}
