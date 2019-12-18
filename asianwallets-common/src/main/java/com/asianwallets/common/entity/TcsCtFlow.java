package com.asianwallets.common.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "tcs_ctflow")
@ApiModel(value = "清算记录表", description = "清算记录表")
public class TcsCtFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ApiModelProperty(value = "编号")
    @Column(name = "CTFlow")
    private String CTFlow;

    @Id
    @ApiModelProperty(value = "参照的订单号")
    @Column(name = "refcnceFlow")
    private String refcnceFlow;

    @ApiModelProperty(value = "交易类型")
    @Column(name = "tradetype")
    private String tradetype;

    @ApiModelProperty(value = "商户号")
    @Column(name = "merchantid")
    private String merchantid;

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "merOrderNo")
    private String merOrderNo;

    @ApiModelProperty(value = "交易币种")
    @Column(name = "txncurrency")
    private String txncurrency;

    @ApiModelProperty(value = "交易金额")
    @Column(name = "txnamount")
    private Double txnamount;

    @ApiModelProperty(value = "revokemount")
    @Column(name = "revokemount")
    private Double revokemount;

    @ApiModelProperty(value = "业务类型")
    @Column(name = "businessType")
    private Integer businessType;

    @ApiModelProperty(value = "资金类型")
    @Column(name = "balancetype")
    private Integer balancetype;//1正常资金，2冻结资金

    @ApiModelProperty(value = "清算状态")
    @Column(name = "CTstate")
    private Integer CTstate;//1待清算，2已清算

    @ApiModelProperty(value = "应该清算时间")
    @Column(name = "shouldCTtime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date shouldCTtime;

    @ApiModelProperty(value = "实际清算时间")
    @Column(name = "actualCTtime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actualCTtime;

    @ApiModelProperty(value = "系统订单号")
    @Column(name = "sysorderid")
    private String sysorderid;

    @ApiModelProperty(value = "手续费")
    @Column(name = "fee")
    private Double fee;

    @ApiModelProperty(value = "手续费币种")
    @Column(name = "feecurrency")
    private String feecurrency;

    @ApiModelProperty(value = "退还收单手续费")
    @Column(name = "refundOrderFee")
    private Double refundOrderFee;

    @ApiModelProperty(value = "退还收单手续费币种")
    @Column(name = "refundOrderFeeCurrency")
    private String refundOrderFeeCurrency;

    @ApiModelProperty(value = "渠道成本")
    @Column(name = "channelCost")
    private Double channelCost;

    @ApiModelProperty(value = "通道成本币种")
    @Column(name = "channelcostcurrency")
    private String channelcostcurrency;

    @ApiModelProperty(value = "系统添加时间")
    @Column(name = "addDatetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addDatetime;

    @ApiModelProperty(value = "交易说明")
    @Column(name = "txndesc")
    private String txndesc;

    @ApiModelProperty(value = "结算金额")
    @Column(name = "sltamount")
    private Double sltamount;

    @ApiModelProperty(value = "结算币种")
    @Column(name = "sltcurrency")
    private String sltcurrency;

    @ApiModelProperty(value = "交易当时汇率")
    @Column(name = "txnexrate")
    private Double txnexrate;

    @ApiModelProperty(value = "备注")
    @Column(name = "remark")
    private String remark;

    @ApiModelProperty(value = "交易状态手续费")
    @Column(name = "gatewayFee")
    private Double gatewayFee;

    @ApiModelProperty(value = "机构编号")
    @Column(name = "organId")
    private String organId;


}
