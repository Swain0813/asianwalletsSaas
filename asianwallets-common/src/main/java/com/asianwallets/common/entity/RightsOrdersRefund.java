package com.asianwallets.common.entity;
import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;


@Data
@Entity
@Table(name =  "rights_orders_refund")
public class RightsOrdersRefund extends BaseEntity {

	@ApiModelProperty(value = "票券id")
	@Column(name ="ticket_id")
	private String ticketId;

	@ApiModelProperty(value = "票券金额")
	@Column(name ="ticket_amount")
	private BigDecimal ticketAmount;

	@ApiModelProperty(value = "票券来源")
	@Column(name ="system_name")
	private String systemName;
    /**
     * 退款状态 1 退款中 2 退款成功 3 退款失败
     */
	@ApiModelProperty(value = "退款状态 1 退款中 2 退款成功 3 退款失败")
	@Column(name ="refund_status")
	private Byte refundStatus;

	@ApiModelProperty(value = "商户请求时间")
	@Column(name ="mer_requset_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date merRequsetTime;

	@ApiModelProperty(value = "系统退款时间")
	@Column(name ="sys_refund_time")
	private Date sysRefundTime;

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
