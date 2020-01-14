package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-04-16 17:58
 **/
@Data
public class ExportSettleCheckAccountDetailVO {

    @ApiModelProperty(value = "订单日期")
    @Column(name = "addDatetime")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date addDatetime;

    @ApiModelProperty(value = "机构订单号")
    @Column(name = "merOrderNo")
    private String merOrderNo;

    @ApiModelProperty(value = "系统流水号")
    @Column(name = "refcnceFlow")
    private String refcnceFlow;

    @ApiModelProperty(value = "交易类型")
    @Column(name = "tradetype")
    private String tradetype;// NT:收单，DT：分账，RF:退款，WD：提款，CL:清算，ST:结算


    @ApiModelProperty(value = "币种")
    @Column(name = "txncurrency")
    private String txncurrency;

    @ApiModelProperty(value = "退还手续费")
    private Double refundOrderFee;

    @ApiModelProperty(value = "退还手续费币种")
    private String refundOrderFeeCurrency;

    @ApiModelProperty(value = "金额")
    @Column(name = "txnamount")
    private Double txnamount;

    @ApiModelProperty(value = "手续费")
    @Column(name = "fee")
    private Double fee;

    @ApiModelProperty(value = "期初余额")
    private double balance;
    @ApiModelProperty(value = "期末余额")
    private double afterBalance;

    @ApiModelProperty(value = "备注")
    private String remark;


}
