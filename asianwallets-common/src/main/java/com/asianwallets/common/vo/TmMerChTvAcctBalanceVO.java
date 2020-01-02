package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;


@Data
@ApiModel(value = "导出结算户余额流水详情", description = "导出结算户余额流水详情")
public class TmMerChTvAcctBalanceVO {

    @ApiModelProperty(value = "交易的流水号")
    private String referenceflow;

    @ApiModelProperty(value = "币种")
    private String currency;


    @ApiModelProperty(value = "交易类型")
    private String tradetype;// NT:收单，DT：分账，RF:退款，WD：提款，CL:清算，ST:结算

    @ApiModelProperty(value = "交易金额")
    private Double txnamount;


    @ApiModelProperty(value = "加入金额")
    private Double income;

    @ApiModelProperty(value = "减少金额")
    private Double outcome;

    @ApiModelProperty(value = "收取手续费")
    private Double fee;

    @ApiModelProperty(value = "原账户余额")
    private Double balance;

    @ApiModelProperty(value = "变动后账户余额")
    private Double afterbalance;

    @ApiModelProperty(value = "系统添加日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sysAddDate;

    @ApiModelProperty(value = "变动时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date balanceTimestamp;

    @ApiModelProperty(value = "备注")
    private String remark;


}
