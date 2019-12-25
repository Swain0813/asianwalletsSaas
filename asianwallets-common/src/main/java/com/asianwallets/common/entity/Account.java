package com.asianwallets.common.entity;
import java.math.BigDecimal;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name="account")
@ApiModel(value = "账户信息表", description = "账户信息表")
public class Account extends BaseEntity {

	@ApiModelProperty(value = "商户编号")
	@Column(name ="merchant_id")
	private String merchantId;

	@ApiModelProperty(value = "商户名称")
	@Column(name ="merchant_name")
	private String merchantName;

	@ApiModelProperty(value = "商户类型")//3普通商户 4代理商户 5集团商户
	@Column(name = "merchant_type")
	private String merchantType;

	@ApiModelProperty(value = "账户编号")
	@Column(name ="account_code")
	private String accountCode;

	@ApiModelProperty(value = "币种")
	@Column(name ="currency")
	private String currency;

	@ApiModelProperty(value = "结算账户余额")
	@Column(name ="settle_balance")
	private BigDecimal settleBalance;

	@ApiModelProperty(value = "清算账户余额")
	@Column(name ="clear_balance")
	private BigDecimal clearBalance;

	@ApiModelProperty(value = "冻结账户余额")
	@Column(name ="freeze_balance")
	private BigDecimal freezeBalance;

	@ApiModelProperty(value = "版本号")
	@Column(name ="version")
	private Long version;

	@ApiModelProperty(value = "启用禁用")
	@Column(name ="enabled")
	private Boolean enabled;


}
