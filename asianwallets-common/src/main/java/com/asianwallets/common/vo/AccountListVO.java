package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "账户信息导出输出参数", description = "账户信息导出输出参数")
public class AccountListVO{

    //@ApiModelProperty(value = "账户id")
    private String id;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    //@ApiModelProperty(value = "账户编号")
    private String accountCode;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "账户余额")
    private BigDecimal balance = BigDecimal.ZERO;

    @ApiModelProperty(value = "结算账户余额")
    private BigDecimal settleBalance = BigDecimal.ZERO;

    @ApiModelProperty(value = "冻结账户余额")
    private BigDecimal freezeBalance = BigDecimal.ZERO;

    @ApiModelProperty(value = "清算账户余额")
    private BigDecimal clearBalance = BigDecimal.ZERO;

    @ApiModelProperty(value = "冻结余额")
    private BigDecimal frozenBalance = BigDecimal.ZERO;

    @ApiModelProperty(value = "保证金余额")
    private BigDecimal marginBalance = BigDecimal.ZERO;

    @ApiModelProperty(value = "最小起结金额")
    private BigDecimal minSettleAmount;

    @ApiModelProperty(value = "自动结算结算开关")//1-开 0-关 默认是0
    private Boolean settleSwitch;

    // 创建时间
    //@ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    //@ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    //@ApiModelProperty(value = "创建者")
    private String creator;

    //@ApiModelProperty(value = "更改者")
    private String modifier;

    //@ApiModelProperty(value = "备注")
    private String remark;
}
