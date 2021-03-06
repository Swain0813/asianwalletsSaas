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
 * 分润流水表
 * </p>
 *
 * @author yx
 * @since 2020-01-03
 */
@Data
@Entity
@Table(name =  "share_benefit_logs")
public class ShareBenefitLogs extends BaseEntity {

    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "订单ID")
	@Column(name ="order_id")
	private String orderId;

	@ApiModelProperty(value = "机构编号")
	@Column(name ="institution_id")
	private String institutionId;

	@ApiModelProperty(value = "机构名称")
	@Column(name ="institution_name")
	private String institutionName;

	@ApiModelProperty(value = "商户名称")
	@Column(name ="merchant_name")
	private String merchantName;

	@ApiModelProperty(value = "商户订单号")
	@Column(name ="merchant_order_id")
	private String merchantOrderId;

	@ApiModelProperty(value = "商户编号")
	@Column(name ="merchant_id")
	private String merchantId;

	@ApiModelProperty(value = "通道编号")
	@Column(name ="channel_code")
	private String channelCode;

	@ApiModelProperty(value = "通道名称")
	@Column(name ="channel_name")
	private String channelName;

	@ApiModelProperty(value = "代理商编号")
	@Column(name ="agent_id")
	private String agentId;

	@ApiModelProperty(value = "代理商名称")
	@Column(name ="agent_name")
	private String agentName;

    /**
     * 代理商类型 1-渠道代理 2-商户代理
     */
	@ApiModelProperty(value = "代理商类型 1-渠道代理 2-商户代理")
	@Column(name ="agent_type")
	private String agentType;

    /**
     * 订单类型 1-收  2-付
     */
	@ApiModelProperty(value = "订单类型 1-收  2-付")
	@Column(name ="order_type")
	private Integer orderType;

	@ApiModelProperty(value = "币种")
	@Column(name ="trade_currency")
	private String tradeCurrency;

	@ApiModelProperty(value = "金额")
	@Column(name ="trade_amount")
	private BigDecimal tradeAmount;

	@ApiModelProperty(value = "代理商手续费")
	@Column(name ="fee")
	private BigDecimal fee;

    /**
     * 分润金额
     */
	@ApiModelProperty(value = "分润金额")
	@Column(name ="share_benefit")
	private BigDecimal shareBenefit;

    /**
     * 1:待分润，2：已分润
     */
	@ApiModelProperty(value = "1:待分润，2：已分润")
	@Column(name ="is_share")
	private Byte isShare;

    /**
     * 分润模式 1-分成 2-费用差
     */
	@ApiModelProperty(value = "分润模式 1-分成 2-费用差")
	@Column(name ="divided_mode")
	private Byte dividedMode;

    /**
     * 分润比例
     */
	@ApiModelProperty(value = "分润比例")
	@Column(name ="divided_ratio")
	private BigDecimal dividedRatio;

	@ApiModelProperty(value = "产品编号")
	@Column(name ="extend1")
	private String extend1;
    /**
     * 备注2
     */
	@ApiModelProperty(value = "产品名称")
	@Column(name ="extend2")
	private String extend2;
    /**
     * 备注3
     */
	@ApiModelProperty(value = "通道编号")
	@Column(name ="extend3")
	private String extend3;
    /**
     * 备注4
     */
	@ApiModelProperty(value = "通道名称")
	@Column(name ="extend4")
	private String extend4;
    /**
     * 备注5
     */
	@ApiModelProperty(value = "商户流水号")
	@Column(name ="extend5")
	private String extend5;
    /**
     * 备注6
     */
	@ApiModelProperty(value = "通道流水号")
	@Column(name ="extend6")
	private String extend6;

	@ApiModelProperty(value = "原订单手续费")
	@Column(name ="extend7")
	private String extend7;


}
