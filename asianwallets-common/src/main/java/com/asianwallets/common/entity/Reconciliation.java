package com.asianwallets.common.entity;

import java.io.Serializable;
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
 * 调账记录表
 * </p>
 *
 * @author yx
 * @since 2019-12-20
 */
@Data
@Entity
@Table(name =  "reconciliation")
public class Reconciliation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 原订单订单号
     */
	@ApiModelProperty(value = "原订单订单号")
	@Column(name ="order_id")
	private String orderId;
    /**
     * 退款订单号
     */
	@ApiModelProperty(value = "退款订单号")
	@Column(name ="refund_order_id")
	private String refundOrderId;
    /**
     * 通道流水号
     */
	@ApiModelProperty(value = "通道流水号")
	@Column(name ="channel_number")
	private String channelNumber;
    /**
     * 退款通道id
     */
	@ApiModelProperty(value = "退款通道id")
	@Column(name ="refund_channel_number")
	private String refundChannelNumber;
    /**
     * 机构订单号-由机构上送
     */
	@ApiModelProperty(value = "机构订单号-由机构上送")
	@Column(name ="merchant_order_id")
	private String merchantOrderId;
    /**
     * 调账类型 1-调入,2-调出
     */
	@ApiModelProperty(value = "调账类型 1-调入,2-调出")
	@Column(name ="reconciliation_type")
	private Integer reconciliationType;
    /**
     * 商户名称
     */
	@ApiModelProperty(value = "商户名称")
	@Column(name ="merchant_name")
	private String merchantName;
    /**
     * 商户编号
     */
	@ApiModelProperty(value = "商户编号")
	@Column(name ="merchant_id")
	private String merchantId;
    /**
     * 请求金额
     */
	@ApiModelProperty(value = "请求金额")
	@Column(name ="amount")
	private BigDecimal amount;
    /**
     * 机构的请求收款币种 必填
     */
	@ApiModelProperty(value = "机构的请求收款币种")
	@Column(name ="currency")
	private String currency;
    /**
     * 调账状态 1-待调账 2-调账成功 3-调账失败
     */
	@ApiModelProperty(value = "调账状态")
	@Column(name ="status")
	private Integer status;
    /**
     * 资金变动类型 1-调账 2-资金冻结 3-资金解冻
     */
	@ApiModelProperty(value = "资金变动类型 1-调账 2-资金冻结 3-资金解冻")
	@Column(name ="change_type")
	private Byte changeType;
    /**
     * 冻结类型 1-冻结 2-预约冻结
     */
	@ApiModelProperty(value = "冻结类型 1-冻结 2-预约冻结")
	@Column(name ="freeze_type")
	private Integer freezeType;
    /**
     * 入账类型  1-清算户 2-结算户 3-冻结户
     */
	@ApiModelProperty(value = "入账类型 1-清算户 2-结算户 3-冻结户")
	@Column(name ="account_type")
	private Integer accountType;
    /**
     * 签名
     */
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
