package com.asianwallets.common.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "tm_merchtvacctbalance")
@ApiModel(value = "账户余额变动记录表", description = "账户余额变动记录表")
public class TmMerChTvAcctBalance{

    @ApiModelProperty(value = "系统流水号")
    @Column(name = "flow")
    private String flow;

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;


    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchantid")
    private String merchantid;

    @ApiModelProperty(value = "商户名称")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "merchant_order_id")
    private String merchantOrderId;


    @ApiModelProperty(value = "所属商户的业务账户编号")
    @Column(name = "mbuaccountId")
    private String mbuaccountId;

    @ApiModelProperty(value = "商户虚拟账户id")
    @Column(name = "vaccounId")
    private String vaccounId;

    @ApiModelProperty(value = "账号类型")
    @Column(name = "type")
    private Integer type;//：1清算账户，2结算账户 3冻结账户

    @ApiModelProperty(value = "业务类型")
    @Column(name = "bussinesstype")
    private Integer bussinesstype;//1分账业务(如果是结算账户时，次处统一为0)

    @ApiModelProperty(value = "资金类型")
    @Column(name = "balancetype")
    private Integer balancetype;//：1正常资金，2冻结资金

    @ApiModelProperty(value = "币种")
    @Column(name = "currency")
    private String currency;

    @ApiModelProperty(value = "交易的流水号")
    @Column(name = "referenceflow")
    private String referenceflow;

    @ApiModelProperty(value = "交易类型")
    @Column(name = "tradetype")
    private String tradetype;// NT:收单，DT：分账，RF:退款，WD：提款，CL:清算，ST:结算, SP:分润 FZ：冻结 TW:解冻

    @ApiModelProperty(value = "交易金额")
    @Column(name = "txnamount")
    private Double txnamount;

    @ApiModelProperty(value = "sltamount")
    @Column(name = "sltamount")
    private Double sltamount;

    @ApiModelProperty(value = "sltcurrency")
    @Column(name = "sltcurrency")
    private String sltcurrency;

    @ApiModelProperty(value = "")
    @Column(name = "sltexrate")
    private Double sltexrate;

    @ApiModelProperty(value = "加入金额")
    @Column(name = "income")
    private Double income;

    @ApiModelProperty(value = "减少金额")
    @Column(name = "outcome")
    private Double outcome;

    @ApiModelProperty(value = "收取手续费")
    @Column(name = "fee")
    private Double fee;

    @ApiModelProperty(value = "退还收单手续费")
    @Column(name = "refundOrderFee")
    private Double refundOrderFee;

    @ApiModelProperty(value = "原账户余额")
    @Column(name = "balance")
    private Double balance;

    @ApiModelProperty(value = "变动后账户余额")
    @Column(name = "afterbalance")
    private Double afterbalance;

    @ApiModelProperty(value = "系统添加日期")
    @Column(name = "sysAddDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sysAddDate;

    @ApiModelProperty(value = "变动时间")
    @Column(name = "balance_timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date balanceTimestamp;

    @ApiModelProperty(value = "remark")
    @Column(name = "remark")
    private String remark;

    @ApiModelProperty(value = "交易状态手续费")
    @Column(name = "gatewayFee")
    private Double gatewayFee;

    @ApiModelProperty(value = "机构编号")
    @Column(name = "organId")
    private String organId;
}
