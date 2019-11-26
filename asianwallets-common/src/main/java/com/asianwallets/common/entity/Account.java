package com.asianwallets.common.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 账户表
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Data
@Entity
@Table(name =  "account")
public class Account extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商户code
     */
	@ApiModelProperty(value = "商户code")
	@Column(name ="merchant_id")
	private String merchantId;
    /**
     * 商户名称
     */
	@ApiModelProperty(value = "商户名称")
	@Column(name ="merchant_name")
	private String merchantName;
    /**
     * 账户编号
     */
	@ApiModelProperty(value = "账户编号")
	@Column(name ="account_code")
	private String accountCode;
    /**
     * 币种
     */
	@ApiModelProperty(value = "币种")
	@Column(name ="currency")
	private String currency;
    /**
     * 结算账户余额
     */
	@ApiModelProperty(value = "结算账户余额")
	@Column(name ="settle_balance")
	private BigDecimal settleBalance;
    /**
     * 清算账户余额
     */
	@ApiModelProperty(value = "清算账户余额")
	@Column(name ="clear_balance")
	private BigDecimal clearBalance;
    /**
     * 冻结账户余额
     */
	@ApiModelProperty(value = "冻结账户余额")
	@Column(name ="freeze_balance")
	private BigDecimal freezeBalance;
    /**
     * 版本号
     */
	@ApiModelProperty(value = "版本号")
	@Column(name ="version")
	private Long version;
    /**
     * 创建时间
     */
	@ApiModelProperty(value = "创建时间")
	@Column(name ="create_time")
	private Date createTime;
    /**
     * 更改时间
     */
	@ApiModelProperty(value = "更改时间")
	@Column(name ="update_time")
	private Date updateTime;

    /**
     * 启用禁用,1是启用 0是禁用
     */
	@ApiModelProperty(value = "启用禁用")
	@Column(name ="enabled")
	private Boolean enabled;


}
