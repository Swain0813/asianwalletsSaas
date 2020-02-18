package com.asianwallets.common.entity;
import java.util.Date;
import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 机构结算单详细表
 * </p>
 *
 * @author yx
 * @since 2020-01-14
 */
@Data
@Entity
@Table(name =  "settle_check_account_detail")
public class SettleCheckAccountDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "结算流水号")
	@Column(name = "STFlow")
	private String STFlow;

	@ApiModelProperty(value = "关联pgworder，退款表，提款表等流水号")
	@Column(name = "refcnceFlow")
	private String refcnceFlow;

	@ApiModelProperty(value = "交易类型")
	@Column(name = "tradetype")
	private String tradetype;// NT:收单，DT：分账，RF:退款，WD：提款，CL:清算，ST:结算 SP:分润

	@ApiModelProperty(value = "商户编号")
	@Column(name = "merchantid")
	private String merchantid;

	@ApiModelProperty(value = "商户订单号")
	@Column(name = "merOrderNo")
	private String merOrderNo;

	@ApiModelProperty(value = "交易币种")
	@Column(name = "txncurrency")
	private String txncurrency;

	@ApiModelProperty(value = "交易金额")
	@Column(name = "txnamount")
	private Double txnamount;

	@ApiModelProperty(value = "交易手续费")
	@Column(name = "fee")
	private Double fee;

	@ApiModelProperty(value = "手续费币种")
	@Column(name = "feecurrency")
	private String feecurrency;

	@ApiModelProperty(value = "退还手续费")
	@Column(name = "refundOrderFee")
	private Double refundOrderFee;

	@ApiModelProperty(value = "退还手续费币种")
	@Column(name = "refundOrderFeeCurrency")
	private String refundOrderFeeCurrency;

	@ApiModelProperty(value = "渠道成本")
	@Column(name = "channelCost")
	private Double channelCost;

	@ApiModelProperty(value = "渠道成本币种")
	@Column(name = "channelcostcurrency")
	private String channelcostcurrency;

	@ApiModelProperty(value = " ")
	@Column(name = "revokemount")
	private Double revokemount;

	@ApiModelProperty(value = "业务类型要和业务账户类型一致")
	@Column(name = "businessType")
	private Integer businessType;

	@ApiModelProperty(value = "资金类型")
	@Column(name = "balancetype")
	private Integer balancetype;//：1正常资金，2冻结资金

	@ApiModelProperty(value = "所属业务账户编号")
	@Column(name = "mbuaccountId")
	private String mbuaccountId;

	@ApiModelProperty(value = "结算账户")
	@Column(name = "accountNo")
	private String accountNo;

	@ApiModelProperty(value = "结算状态")
	@Column(name = "STstate")
	private Integer STstate;// 1未结算，2已结算

	@ApiModelProperty(value = "应结算时间")
	@Column(name = "shouldSTtime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date shouldSTtime;

	@ApiModelProperty(value = "实际结算时间")
	@Column(name = "actualSTtime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date actualSTtime;

	@ApiModelProperty(value = "系统订单号")
	@Column(name = "sysorderid")
	private String sysorderid;

	@ApiModelProperty(value = "系统添加时间")
	@Column(name = "addDatetime")
	private Date addDatetime;

	@ApiModelProperty(value = "交易说明")
	@Column(name = "txndesc")
	private String txndesc;

	@ApiModelProperty(value = "结算金额")
	@Column(name = "sltamount")
	private Double sltamount;

	@ApiModelProperty(value = "结算币种")
	@Column(name = "sltcurrency")
	private String sltcurrency;

	@ApiModelProperty(value = "交易当时汇率，国际业务")
	@Column(name = "txnexrate")
	private Double txnexrate;

	@ApiModelProperty(value = "备注")
	@Column(name = "remark")
	private String remark;

	@ApiModelProperty(value = "交易状态手续费")
	@Column(name = "gatewayFee")
	private Double gatewayFee;

	@ApiModelProperty(value = "机构编号")
	@Column(name = "organId")
	private String organId;

	@ApiModelProperty(value = "是否需要处理清除")
	@Column(name = "needClear")
	private Integer needClear;//：1不需要，2需要


}
