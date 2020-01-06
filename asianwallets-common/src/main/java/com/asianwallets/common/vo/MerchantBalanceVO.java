package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "商户余额查询VO", description = "商户余额查询VO")
public class MerchantBalanceVO {

    @ApiModelProperty(value = "系统添加日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sysAddDate;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "商户订单号")
    private String merchantOrderId;

    @ApiModelProperty(value = "系统流水号")
    private String referenceFlow;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "交易金额")
    private Double txnAmount;

    @ApiModelProperty(value = "收取手续费")
    private Double fee;

    @ApiModelProperty(value = "交易类型")
    private String tradeType;// NT:收单，DT：分账，RF:退款，WD：提款，CL:清算，ST:结算, SP:分润 FZ：冻结 TW:解冻

    @ApiModelProperty(value = "加入金额")
    private Double income;

    @ApiModelProperty(value = "减少金额")
    private Double outcome;

    @ApiModelProperty(value = "原账户余额")
    private Double balance;

    @ApiModelProperty(value = "变动后账户余额")
    private Double afterBalance;

    @ApiModelProperty(value = "备注")
    private String remark;

}
