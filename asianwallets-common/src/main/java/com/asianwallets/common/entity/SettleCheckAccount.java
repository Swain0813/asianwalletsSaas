package com.asianwallets.common.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 机构结算单表
 * </p>
 *
 * @author yx
 * @since 2020-01-14
 */
@Data
@Entity
@Table(name =  "settle_check_account")
public class SettleCheckAccount extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商户编号
     */
	@ApiModelProperty(value = "商户编号")
	@Column(name ="merchant_id")
	private String merchantId;
    /**
     * 结算时间
     */
	@ApiModelProperty(value = "结算时间")
	@Column(name ="check_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date checkTime;

	@ApiModelProperty(value = "count")
	@Column(name ="count")
	private Integer count;
    /**
     * 币种
     */
	@ApiModelProperty(value = "币种")
	@Column(name ="currency")
	private String currency;
    /**
     * 金额
     */
	@ApiModelProperty(value = "金额")
	@Column(name ="amount")
	private BigDecimal amount;
    /**
     * 手续费
     */
	@ApiModelProperty(value = "手续费")
	@Column(name ="fee")
	private BigDecimal fee;
    /**
     * 退还收单手续费
     */
	@ApiModelProperty(value = "退还收单手续费")
	@Column(name ="refund_order_fee")
	private BigDecimal refundOrderFee;
    /**
     * 期初金额
     */
	@ApiModelProperty(value = "期初金额")
	@Column(name ="initial_amount")
	private BigDecimal initialAmount;
    /**
     * 期末金额
     */
	@ApiModelProperty(value = "期末金额")
	@Column(name ="final_amount")
	private BigDecimal finalAmount;

}
