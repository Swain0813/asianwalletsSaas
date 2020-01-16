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
@Table(name = "trade_check_account")
@ApiModel(value = "交易对账单", description = "交易对账单")
public class TradeCheckAccount extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "交易时间")
    @Column(name = "trade_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeTime;

    @ApiModelProperty(value = "订单币种")
    @Column(name = "currency")
    private String currency;

    @ApiModelProperty(value = "总收单金额")
    @Column(name = "total_trade_amount")
    private BigDecimal totalTradeAmount;

    @ApiModelProperty(value = "总收单笔数")
    @Column(name = "total_trade_count")
    private Integer totalTradeCount;

    @ApiModelProperty(value = "总退款金额")
    @Column(name = "total_refund_amount")
    private BigDecimal totalRefundAmount;

    @ApiModelProperty(value = "总退款笔数")
    @Column(name = "total_refund_count")
    private Integer totalRefundCount;

    @ApiModelProperty(value = "手续费")
    @Column(name = "fee")
    private BigDecimal fee;

}