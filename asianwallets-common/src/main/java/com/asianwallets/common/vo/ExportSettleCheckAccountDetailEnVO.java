package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * 机构结算对账详情的英文版输入参数
 */
@Data
@ApiModel(value = "机构结算对账详情的英文版输入参数", description = "机构结算对账详情的英文版输入参数")
public class ExportSettleCheckAccountDetailEnVO {

    @ApiModelProperty(value = "Order Date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date addDatetime;

    @ApiModelProperty(value = "Institution Order Id")
    private String merOrderNo;

    @ApiModelProperty(value = "AW Order ID")
    private String refcnceFlow;

    @ApiModelProperty(value = "tradetype")
    @Column(name = "tradetype")
    private String tradetype;// NT:收单，DT：分账，RF:退款，WD：提款，CL:清算，ST:结算

    @ApiModelProperty(value = "Currency")
    private String txncurrency;

    @ApiModelProperty(value = "Order Amount")
    private Double txnamount;

    @ApiModelProperty(value = "Fee")
    private Double fee;

    @ApiModelProperty(value = "refundOrderFee")
    private Double refundOrderFee;

    @ApiModelProperty(value = "refundOrderFeeCurrency")
    private String refundOrderFeeCurrency;

    @ApiModelProperty(value = "balance")
    private double balance;
    @ApiModelProperty(value = "afterBalance")
    private double afterBalance;

    @ApiModelProperty(value = "remark")
    private String remark;


}
