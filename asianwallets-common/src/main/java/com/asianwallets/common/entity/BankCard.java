package com.asianwallets.common.entity;

import java.util.Date;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 银行卡表
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Data
@Entity
@Table(name =  "bank_card")
public class BankCard extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 机构code
     */
	@ApiModelProperty(value = "机构code")
	@Column(name ="merchant_id")
	private String merchantId;
    /**
     * 账户编号
     */
	@ApiModelProperty(value = "账户编号")
	@Column(name ="account_code")
	private String accountCode;
    /**
     * 开户账号
     */
	@ApiModelProperty(value = "开户账号")
	@Column(name ="bank_account_code")
	private String bankAccountCode;
    /**
     * 开户名称
     */
	@ApiModelProperty(value = "开户名称")
	@Column(name ="account_name")
	private String accountName;
    /**
     * 结算币种
     */
	@ApiModelProperty(value = "结算币种")
	@Column(name ="settle_currency")
	private String settleCurrency;
    /**
     * 银行卡币种
     */
	@ApiModelProperty(value = "银行卡币种")
	@Column(name ="bank_currency")
	private String bankCurrency;
    /**
     * iban
     */
	@ApiModelProperty(value = "iban")
	@Column(name ="iban")
	private String iban;
    /**
     * bank_code
     */
	@ApiModelProperty(value = "bank_code")
	@Column(name ="bank_code")
	private String bankCode;
    /**
     * 开户行地址
     */
	@ApiModelProperty(value = "开户行地址")
	@Column(name ="bank_address")
	private String bankAddress;
    /**
     * 银行卡账单地址
     */
	@ApiModelProperty(value = "银行卡账单地址")
	@Column(name ="bank_card_billing_address")
	private String bankCardBillingAddress;
    /**
     * 性质
     */
	@ApiModelProperty(value = "nature")
	@Column(name ="nature")
	private Byte nature;
    /**
     * 收款人地区/国家
     */
	@ApiModelProperty(value = "收款人地区/国家")
	@Column(name ="receiver_country")
	private String receiverCountry;
    /**
     * 收款人地址
     */
	@ApiModelProperty(value = "收款人地址")
	@Column(name ="receiver_address")
	private String receiverAddress;
    /**
     * 收款人
     */
	@ApiModelProperty(value = "收款人")
	@Column(name ="receiver")
	private String receiver;
    /**
     * 开户行名称
     */
	@ApiModelProperty(value = "开户行名称")
	@Column(name ="bank_name")
	private String bankName;
    /**
     * Swift Code
     */
	@ApiModelProperty(value = "Swift Code")
	@Column(name ="swift_code")
	private String swiftCode;
    /**
     * 是否为中间行银行
     */
	@ApiModelProperty(value = "是否为中间行银行")
	@Column(name ="intermediary_bank_default")
	private String intermediaryBankDefault;
    /**
     * 中间行银行编码
     */
	@ApiModelProperty(value = "中间行银行编码")
	@Column(name ="intermediary_bank_code")
	private String intermediaryBankCode;
    /**
     * 中间行银行名称
     */
	@ApiModelProperty(value = "中间行银行名称")
	@Column(name ="intermediary_bank_name")
	private String intermediaryBankName;
    /**
     * 中间行银行地址
     */
	@ApiModelProperty(value = "中间行银行地址")
	@Column(name ="intermediary_bank_address")
	private String intermediaryBankAddress;
    /**
     * 中间行银行账户
     */
	@ApiModelProperty(value = "中间行银行账户")
	@Column(name ="intermediary_bank_account_no")
	private String intermediaryBankAccountNo;
    /**
     * 中间行银行城市
     */
	@ApiModelProperty(value = "中间行银行城市")
	@Column(name ="intermediary_bank_country")
	private String intermediaryBankCountry;
    /**
     * 中间行银行其他code
     */
	@ApiModelProperty(value = "中间行银行其他code")
	@Column(name ="intermediary_other_code")
	private String intermediaryOtherCode;
    /**
     * 是否设为默认银行卡
     */
	@ApiModelProperty(value = "是否设为默认银行卡")
	@Column(name ="default_flag")
	private Boolean defaultFlag;
    /**
     * 创建时间
     */
	@ApiModelProperty(value = "创建时间")
	@Column(name ="create_time")
	private Date createTime;
    /**
     * 更新时间
     */
	@ApiModelProperty(value = "更新时间")
	@Column(name ="update_time")
	private Date updateTime;

    /**
     * 启用禁用
     */
	@ApiModelProperty(value = "启用禁用")
	@Column(name ="enabled")
	private Boolean enabled;

}
