package com.asianwallets.common.entity;

import java.math.BigDecimal;
import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *
 * </p>
 *
 * @author yx
 * @since 2019-12-09
 */
@Data
@Entity
@Table(name =  "merchant_product")
public class MerchantProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商户id
     */
	@ApiModelProperty(value = "商户id")
	@Column(name ="merchant_id")
	private String merchantId;
    /**
     * 产品id
     */
	@ApiModelProperty(value = "产品id")
	@Column(name ="product_id")
	private String productId;
    /**
     * 商户名称
     */
	@ApiModelProperty(value = "商户名称")
	@Column(name ="merchant_name")
	private String merchantName;
    /**
     * 商品简称
     */
	@ApiModelProperty(value = "商品简称")
	@Column(name ="product_abbrev")
	private String productAbbrev;
    /**
     * 交易场景：1-线上 2-线下
     */
	@ApiModelProperty(value = "交易场景：1-线上 2-线下")
	@Column(name ="trade_direction")
	private Integer tradeDirection;
    /**
     * 交易类型（1-收、2-付）
     */
	@ApiModelProperty(value = "交易类型（1-收、2-付）")
	@Column(name ="trans_type")
	private Integer transType;
    /**
     * 支付方式(银联，网银，...）
     */
	@ApiModelProperty(value = "支付方式(银联，网银，...）")
	@Column(name ="pay_type")
	private String payType;
    /**
     * 费率类型 (1-单笔费率,2-单笔定额)
     */
	@ApiModelProperty(value = "费率类型 (1-单笔费率,2-单笔定额)")
	@Column(name ="rate_type")
	private String rateType;
    /**
     * 费率最小值
     */
	@ApiModelProperty(value = "费率最小值")
	@Column(name ="min_tate")
	private BigDecimal minTate;
    /**
     * 费率最大值
     */
	@ApiModelProperty(value = "费率最大值")
	@Column(name ="max_tate")
	private BigDecimal maxTate;
    /**
     * 费率
     */
	@ApiModelProperty(value = "费率")
	@Column(name ="rate")
	private BigDecimal rate;
    /**
     * 附加值
     */
	@ApiModelProperty(value = "附加值")
	@Column(name ="add_value")
	private BigDecimal addValue;
    /**
     * 单笔交易限额
     */
	@ApiModelProperty(value = "单笔交易限额")
	@Column(name ="limit_amount")
	private BigDecimal limitAmount;
    /**
     * 日累计交易笔数
     */
	@ApiModelProperty(value = "日累计交易笔数")
	@Column(name ="daily_trading_count")
	private Integer dailyTradingCount;
    /**
     * 浮动率
     */
	@ApiModelProperty(value = "浮动率")
	@Column(name ="float_rate")
	private BigDecimal floatRate;
    /**
     * 分润比例
     */
	@ApiModelProperty(value = "分润比例")
	@Column(name ="divided_ratio")
	private BigDecimal dividedRatio;
    /**
     * 分润模式 1-分成 2-费用差
     */
	@ApiModelProperty(value = "分润模式 1-分成 2-费用差")
	@Column(name ="divided_mode")
	private Integer dividedMode;
    /**
     * 手续费付款方 1 ：内扣 2 ：外扣
     */
	@ApiModelProperty(value = "手续费付款方 1 ：内扣 2 ：外扣")
	@Column(name ="fee_payer")
	private Integer feePayer;
    /**
     * 退款是否收费
     */
	@ApiModelProperty(value = "退款是否收费")
	@Column(name ="refund_default")
	private Boolean refundDefault;
    /**
     * 退款费率类型
     */
	@ApiModelProperty(value = "退款费率类型")
	@Column(name ="refund_rate_type")
	private String refundRateType;
    /**
     * 退款手续费最小值
     */
	@ApiModelProperty(value = "退款手续费最小值")
	@Column(name ="refund_min_tate")
	private BigDecimal refundMinTate;
    /**
     * 退款手续费最大值
     */
	@ApiModelProperty(value = "退款手续费最大值")
	@Column(name ="refund_max_tate")
	private BigDecimal refundMaxTate;
    /**
     * 退款费率
     */
	@ApiModelProperty(value = "退款费率")
	@Column(name ="refund_rate")
	private BigDecimal refundRate;
    /**
     * 退款附加值
     */
	@ApiModelProperty(value = "退款附加值")
	@Column(name ="refund_add_value")
	private BigDecimal refundAddValue;
    /**
     * 结算周期 (T+0,T+3,...)
     */
	@ApiModelProperty(value = "结算周期 (T+0,T+3,...)")
	@Column(name ="settle_cycle")
	private String settleCycle;
    /**
     * 审核状态 1-待审核 2-审核通过 3-审核不通过
     */
	@ApiModelProperty(value = "审核状态 1-待审核 2-审核通过 3-审核不通过")
	@Column(name ="audit_status")
	private Byte auditStatus;
    /**
     * 审核备注
     */
	@ApiModelProperty(value = "审核备注")
	@Column(name ="audit_remark")
	private String auditRemark;
	@ApiModelProperty(value = "生效时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name ="effect_time")
	private String effectTime;

	@ApiModelProperty(value = "enabled")
	@Column(name ="enabled")
	private Boolean enabled;

	@ApiModelProperty(value = "ext1")
	@Column(name ="ext1")
	private String ext1;
	@ApiModelProperty(value = "ext2")
	@Column(name ="ext2")
	private String ext2;
	@ApiModelProperty(value = "ext3")
	@Column(name ="ext3")
	private String ext3;
	@ApiModelProperty(value = "ext4")
	@Column(name ="ext4")
	private String ext4;
	@ApiModelProperty(value = "ext5")
	@Column(name ="ext5")
	private String ext5;
	@ApiModelProperty(value = "ext6")
	@Column(name ="ext6")
	private String ext6;
	@ApiModelProperty(value = "ext7")
	@Column(name ="ext7")
	private String ext7;



}
