package com.asianwallets.common.entity;
import java.math.BigDecimal;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 产品表
 */
@Data
@Entity
@Table(name =  "product")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "产品编号")
	@Column(name ="product_code")
	private Integer productCode;

	@ApiModelProperty(value = "产品名称")
	@Column(name ="product_name")
	private String productName;

	@ApiModelProperty(value = "产品详情logo")
	@Column(name ="product_details_logo")
	private String productDetailsLogo;

	@ApiModelProperty(value = "产品打印logo")
	@Column(name ="product_print_logo")
	private String productPrintLogo;

	@ApiModelProperty(value = "产品图标")
	@Column(name ="product_img")
	private String productImg;


	@ApiModelProperty(value = "产品类型")//产品类型:1-收款 2-付款
	@Column(name ="trans_type")
	private Integer transType;

	@ApiModelProperty(value = "交易类型")//1-线上 2-线下
	@Column(name ="trade_direction")
	private Integer tradeDirection;

	@ApiModelProperty(value = "支付方式(银联，网银，...）")
	@Column(name ="pay_type")
	private String payType;

	@ApiModelProperty(value = "币种")
	@Column(name ="currency")
	private String currency;

	@ApiModelProperty(value = "日累计交易总额")
	@Column(name ="daily_total_amount")
	private BigDecimal dailyTotalAmount;

	@ApiModelProperty(value = "日累计交易笔数")
	@Column(name ="daily_trading_count")
	private Integer dailyTradingCount;


	@ApiModelProperty(value = "单笔交易限额")
	@Column(name ="limit_amount")
	private BigDecimal limitAmount;

	@ApiModelProperty(value = "启用禁用")
	@Column(name ="enabled")
	private Boolean enabled;



}
