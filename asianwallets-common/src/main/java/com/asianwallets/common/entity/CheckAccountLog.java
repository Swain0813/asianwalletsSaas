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
@Table(name = "check_account_log")
@ApiModel(value = "对账日志表", description = "对账日志表")
public class CheckAccountLog extends BaseEntity {

    @ApiModelProperty(value = "对账区间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "check_time")
    private Date checkTime;

    @ApiModelProperty(value = "对账文件名称")
    @Column(name = "check_file_name")
    private String checkFileName;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    @Column(name = "channel_name")
    private String channelName;

    @ApiModelProperty(value = "产品名称")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty(value = "产品编码")
    @Column(name = "product_code")
    private Integer productCode;

    @ApiModelProperty(value = "通道交易笔数")
    @Column(name = "cha_trade_count")
    private Integer chaTradeCount;

    @ApiModelProperty(value = "系统交易笔数")
    @Column(name = "sys_trade_count")
    private Integer sysTradeCount;

    @ApiModelProperty(value = "币种")
    @Column(name = "currency")
    private String currency;

    @ApiModelProperty(value = "通道交易总金额")
    @Column(name = "cha_total_amount")
    private BigDecimal chaTotalAmount;

    @ApiModelProperty(value = "系统交易总金额")
    @Column(name = "sys_trade_amount")
    private BigDecimal sysTradeAmount;

    @ApiModelProperty(value = "通道交易手续费")
    @Column(name = "cha_total_fee")
    private BigDecimal chaTotalFee;

    @ApiModelProperty(value = "系统交易手续费")
    @Column(name = "sys_trade_fee")
    private BigDecimal sysTradeFee;

    @ApiModelProperty(value = "差错笔数")
    @Column(name = "error_count")
    private Integer errorCount;

    @ApiModelProperty(value = "差错金额")
    @Column(name = "error_amount")
    private BigDecimal errorAmount;
}