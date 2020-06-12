package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * <p>
 *
 * </p>
 *
 * @author yx
 * @since 2019-12-30
 */
@Data
@Entity
@Table(name =  "rights_orders")
public class RightsOrders extends BaseEntity {

	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "机构编号")
	@Column(name ="institution_id")
	private String institutionId;

	@ApiModelProperty(value = "机构名称")
	@Column(name ="institution_name")
	private String institutionName;

	@ApiModelProperty(value = "二级商户编号")
	@Column(name ="merchant_id")
	private String merchantId;

	@ApiModelProperty(value = "二级商户名称")
	@Column(name ="merchant_name")
	private String merchantName;

	@ApiModelProperty(value = "店铺编号")
	@Column(name ="shop_id")
	private String shopId;

	@ApiModelProperty(value = "店铺名")
	@Column(name ="shop_name")
	private String shopName;

	@ApiModelProperty(value = "请求订单号")
	@Column(name ="request_order_no")
	private String requestOrderNo;

	@ApiModelProperty(value = "机构订单号")
	@Column(name ="order_no")
	private String orderNo;

	@ApiModelProperty(value = "订单金额")
	@Column(name ="order_amount")
	private BigDecimal orderAmount;

	@ApiModelProperty(value = "抵扣金额")
	@Column(name ="deduction_amount")
	private BigDecimal deductionAmount;

	@ApiModelProperty(value = "实际支付金额")
	@Column(name ="actual_amount")
	private BigDecimal actualAmount;
	/**
	 * 权益币种
	 */
	@ApiModelProperty(value = "权益币种")
	@Column(name ="rights_currency")
	private String rightsCurrency;

	@ApiModelProperty(value = "核销金额")
	@Column(name ="cancel_verification_amount")
	private BigDecimal cancelVerificationAmount;

	@ApiModelProperty(value = "平台名称 售卖权益的平台")
	@Column(name ="system_name")
	private String systemName;

	@ApiModelProperty(value = "票券编号")
	@Column(name ="ticket_id")
	private String ticketId;

	@ApiModelProperty(value = "票券金额")
	@Column(name ="ticket_amount")
	private BigDecimal ticketAmount;

	@ApiModelProperty(value = "票券数量")
	@Column(name ="ticket_num")
	private Integer ticketNum;
	/**
	 * 权益类型：1-满减 2-折扣 3-套餐 4-定额
	 */
	@ApiModelProperty(value = "权益类型")
	@Column(name ="rights_type")
	private Byte rightsType;

	@ApiModelProperty(value = "用户id")
	@Column(name ="user_id")
	private String userId;

	@ApiModelProperty(value = "平台交易流水号")
	@Column(name ="system_order_id")
	private String systemOrderId;

	@ApiModelProperty(value = "OTA开团ID")
	@Column(name ="deal_id")
	private String dealId;
	/**
	 * 核销状态：1.核销中 2.核销成功 3.核销失败
	 */
	@ApiModelProperty(value = "核销状态")
	@Column(name ="status")
	private Byte status;

	@ApiModelProperty(value = "服务器回调地址")
	@Column(name ="server_url")
	private String serverUrl;

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
