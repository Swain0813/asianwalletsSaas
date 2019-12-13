package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author shen
 * @Description: 费率表
 */
@Data
@Table(name = "charges")
@ApiModel(value = "费率", description = "费率")
public class Charges extends BaseEntity {

    @ApiModelProperty(value = "费率类型，0：单笔定额，1：单笔费率")
    @Column(name = "rate_type")
    private String rateType;

    @ApiModelProperty(value = "保底金额,默认0")
    @Column(name = "guaranteed_amount")
    private BigDecimal guaranteedAmount;

    @ApiModelProperty(value = "封顶金额,默认99999999")
    @Column(name = "capping_amount")
    private BigDecimal cappingAmount;

    @ApiModelProperty(value = "附加值")
    @Column(name = "added_value")
    private BigDecimal addedValue;

    @ApiModelProperty(value = "付款方")
    @Column(name = "fee_payer")
    private String feePayer;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;
}