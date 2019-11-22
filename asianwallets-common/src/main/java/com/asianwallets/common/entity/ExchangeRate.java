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
@Table(name = "exchange_rate")
@ApiModel(value = "汇率", description = "汇率")
public class ExchangeRate extends BaseEntity {

    @ApiModelProperty(value = "买入汇率")
    @Column(name = "buy_rate")
    private BigDecimal buyRate;

    @ApiModelProperty(value = "卖出汇率")
    @Column(name = "sale_rate")
    private BigDecimal saleRate;

    @ApiModelProperty(value = "中间价")
    @Column(name = "middle_rate")
    private BigDecimal middleRate;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "本位币种")
    @Column(name = "local_currency")
    private String localCurrency;

    @ApiModelProperty(value = "目标币种")
    @Column(name = "foreign_currency")
    private String foreignCurrency;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "using_time")
    private Date usingTime;

    @ApiModelProperty(value = "失效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "overdue_time")
    private Date overdueTime;

}