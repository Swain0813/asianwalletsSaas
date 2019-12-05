package com.asianwallets.common.entity;

import java.math.BigDecimal;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 产品表
 * </p>
 *
 * @author yx
 * @since 2019-12-05
 */
@Data
@Entity
@Table(name =  "product")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 产品编号
     */
	@ApiModelProperty(value = "产品编号")
	@Column(name ="product_code")
	private Integer productCode;
    /**
     * 产品名称
     */
	@ApiModelProperty(value = "产品名称")
	@Column(name ="product_name")
	private String productName;
    /**
     * 产品详情logo
     */
	@ApiModelProperty(value = "产品详情logo")
	@Column(name ="product_details_logo")
	private String productDetailsLogo;
    /**
     * 产品打印logo
     */
	@ApiModelProperty(value = "产品打印logo")
	@Column(name ="product_print_logo")
	private String productPrintLogo;
    /**
     * 产品图标
     */
	@ApiModelProperty(value = "产品图标")
	@Column(name ="product_img")
	private String productImg;
    /**
     * 交易类型（1-收、2-付）
     */
	@ApiModelProperty(value = "交易类型（1-收、2-付）")
	@Column(name ="trans_type")
	private Integer transType;
    /**
     * 交易场景：1-线上 2-线下
     */
	@ApiModelProperty(value = "交易场景：1-线上 2-线下")
	@Column(name ="trade_direction")
	private Integer tradeDirection;
    /**
     * 支付方式(银联，网银，...）
     */
	@ApiModelProperty(value = "支付方式(银联，网银，...）")
	@Column(name ="pay_type")
	private String payType;
    /**
     * 币种
     */
	@ApiModelProperty(value = "币种")
	@Column(name ="currency")
	private String currency;
    /**
     * 日累计交易总额
     */
	@ApiModelProperty(value = "日累计交易总额")
	@Column(name ="daily_total_amount")
	private BigDecimal dailyTotalAmount;
    /**
     * 日累计交易笔数
     */
	@ApiModelProperty(value = "日累计交易笔数")
	@Column(name ="daily_trading_count")
	private Integer dailyTradingCount;
    /**
     * 单笔交易限额
     */
	@ApiModelProperty(value = "单笔交易限额")
	@Column(name ="limit_amount")
	private BigDecimal limitAmount;
	/**
	 * 启用禁用
	 */
	@ApiModelProperty(value = "启用禁用")
	@Column(name ="enabled")
	private Boolean enabled;



}
