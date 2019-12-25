package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "账户查询输入参数", description = "账户查询输入参数")
public class AccountSearchDTO extends BasePageHelper {

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
