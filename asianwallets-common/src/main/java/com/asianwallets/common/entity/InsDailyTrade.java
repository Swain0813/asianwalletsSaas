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
@Table(name = "ins_daily_trade")
@ApiModel(value = "机构日交易汇总表", description = "机构日交易汇总表")
public class InsDailyTrade extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    @Column(name = "channel_name")
    private String channelName;

    @ApiModelProperty(value = "订单币种")
    @Column(name = "order_currency")
    private String orderCurrency;

    @ApiModelProperty(value = "订单总笔数")
    @Column(name = "total_count")
    private Integer totalCount;

    @ApiModelProperty(value = "订单总金额")
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "订单总手续费")
    @Column(name = "total_fee")
    private BigDecimal totalFee;

    @ApiModelProperty(value = "交易时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "trade_time")
    private Date tradeTime;

    @ApiModelProperty(value = "扩展1")
    @Column(name = "extend1")
    private String extend1;

    @ApiModelProperty(value = "扩展2")
    @Column(name = "extend2")
    private String extend2;

    @ApiModelProperty(value = "扩展3")
    @Column(name = "extend3")
    private String extend3;

}