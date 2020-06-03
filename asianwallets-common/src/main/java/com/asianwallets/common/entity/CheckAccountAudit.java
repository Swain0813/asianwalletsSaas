package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "check_account_audit")
@ApiModel(value = "对账复核表", description = "对账复核表")
public class CheckAccountAudit extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_code")
    private String institutionCode;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "产品编号")
    @Column(name = "product_code")
    private Integer productCode;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "平台订单id")
    @Column(name = "u_order_id")
    private String uOrderId;

    @ApiModelProperty(value = "平台通道流水号")
    @Column(name = "u_channel_number")
    private String uChannelNumber;

    @ApiModelProperty(value = "上游订单id")
    @Column(name = "c_order_id")
    private String cOrderId;

    @ApiModelProperty(value = "上游通道流水号")
    @Column(name = "c_channel_number")
    private String cChannelNumber;

    @ApiModelProperty(value = "交易类型 1：收单 2:退款")
    @Column(name = "trade_type")
    private Integer tradeType;

    @ApiModelProperty(value = "平台交易金额")
    @Column(name = "u_trade_amount")
    private BigDecimal uTradeAmount;

    @ApiModelProperty(value = "平台交易币种")
    @Column(name = "u_trade_currency")
    private String uTradeCurrency;

    @ApiModelProperty(value = "平台手续费")
    @Column(name = "u_fee")
    private BigDecimal uFee;

    @ApiModelProperty(value = "平台订单状态")
    @Column(name = "u_status")
    private Byte uStatus;

    @ApiModelProperty(value = "系统订单状态")
    @Column(name = "c_status")
    private Byte cStatus;

    @ApiModelProperty(value = "上游交易金额")
    @Column(name = "c_trade_amount")
    private BigDecimal cTradeAmount;

    @ApiModelProperty(value = "上游交易币种")
    @Column(name = "c_trade_currency")
    private String cTradeCurrency;

    @ApiModelProperty(value = "上游手续费")
    @Column(name = "c_fee")
    private BigDecimal cFee;

    @ApiModelProperty(value = "错误类型")
    @Column(name = "error_type")
    private Integer errorType; //1,待对账；2，差错处理 3，补单  4，对账成功

    @ApiModelProperty(value = "复核状态")
    @Column(name = "audit_status")
    private Integer auditStatus; //1,待复核 2,复核通过，3复核不通过

    @ApiModelProperty(value = "交易时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "trade_time")
    private Date tradeTime;

    @ApiModelProperty(value = "差错处理备注")
    @Column(name = "remark1")
    private String remark1;

    @ApiModelProperty(value = "差错复核备注")
    @Column(name = "remark2")
    private String remark2;

}