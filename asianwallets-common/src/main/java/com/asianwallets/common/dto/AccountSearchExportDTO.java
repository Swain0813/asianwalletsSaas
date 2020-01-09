package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "结算账户查询导出输入参数", description = "结算账户查询导出输入参数")
public class AccountSearchExportDTO {

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "商户类型")
    private String merchantType;

    @ApiModelProperty(value = "账户Id")
    private String accountId;

    @ApiModelProperty(value = "账户币种")
    private String currency;

}
