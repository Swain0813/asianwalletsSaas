package com.asianwallets.common.vo;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "提款vo", description = "提款vo")
public class WithdrawalVO extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "账户编号")
    private String accountCode;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "结算账户余额")
    private BigDecimal settleBalance;

    @ApiModelProperty(value = "清算账户余额")
    private BigDecimal clearBalance;

    @ApiModelProperty(value = "冻结账户余额")
    private BigDecimal freezeBalance;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "自动结算结算开关")//1-开 0-关 默认是0
    private Boolean settleSwitch;

    @ApiModelProperty(value = "最小起结金额")
    private BigDecimal minSettleAmount;

    @ApiModelProperty(value = "禁用启用")
    private Boolean enabled;

    @ApiModelProperty(value = "可用余额")
    private BigDecimal availableBalance;

}
