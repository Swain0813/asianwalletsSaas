package com.asianwallets.common.entity;
import java.math.BigDecimal;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 调账记录表
 */
@Data
@Entity
@Table(name =  "reconciliation")
public class Reconciliation extends BaseEntity {


	@ApiModelProperty(value = "原订单订单号")
	@Column(name ="order_id")
	private String orderId;

	@ApiModelProperty(value = "退款订单号")
	@Column(name ="refund_order_id")
	private String refundOrderId;

	@ApiModelProperty(value = "通道流水号")
	@Column(name ="channel_number")
	private String channelNumber;

	@ApiModelProperty(value = "退款通道流水号")
	@Column(name ="refund_channel_number")
	private String refundChannelNumber;

	@ApiModelProperty(value = "商户订单号")
	@Column(name ="merchant_order_id")
	private String merchantOrderId;

	@ApiModelProperty(value = "调账类型")//1-调入,2-调出
	@Column(name ="reconciliation_type")
	private Integer reconciliationType;

	@ApiModelProperty(value = "商户编号")
	@Column(name ="merchant_id")
	private String merchantId;

	@ApiModelProperty(value = "商户名称")
	@Column(name ="merchant_name")
	private String merchantName;

	@ApiModelProperty(value = "请求金额")
	@Column(name ="amount")
	private BigDecimal amount;

	@ApiModelProperty(value = "请求币种")
	@Column(name ="currency")
	private String currency;

	@ApiModelProperty(value = "调账状态")//1-待调账 2-调账成功 3-调账失败
	@Column(name ="status")
	private Integer status;

	@ApiModelProperty(value = "资金变动类型")//1-调账 2-资金冻结 3-资金解冻
	@Column(name ="change_type")
	private Byte changeType;


	@ApiModelProperty(value = "冻结类型")//1-冻结 2-预约冻结
	@Column(name ="freeze_type")
	private Integer freezeType;

	@ApiModelProperty(value = "入账类型")//1-清算户 2-结算户 3-冻结户
	@Column(name ="account_type")
	private Integer accountType;

	@ApiModelProperty(value = "签名")
	@Column(name ="sign")
	private String sign;

	@ApiModelProperty(value = "remark1")
	@Column(name ="remark1")
	private String remark1;

	@ApiModelProperty(value = "remark2")
	@Column(name ="remark2")
	private String remark2;

	@ApiModelProperty(value = "remark3")
	@Column(name ="remark3")
	private String remark3;
}
