package com.asianwallets.common.entity;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "settle_order")
@ApiModel(value = "商户结算表", description = "商户结算表")
public class SettleOrder extends BaseEntity {

    @ApiModelProperty(value = "批次号")
    @Column(name = "batch_no")
    private String batchNo;

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "交易币种")
    @Column(name = "txncurrency")
    private String txncurrency;

    @ApiModelProperty(value = "交易金额")
    @Column(name = "txnamount")
    private BigDecimal txnamount;

    @ApiModelProperty(value = "结算账户")
    @Column(name = "account_code")
    private String accountCode;

    @ApiModelProperty(value = "账户名")
    @Column(name = "account_name")
    private String accountName;

    @ApiModelProperty(value = "银行名称")
    @Column(name = "bank_name")
    private String bankName;

    @ApiModelProperty(value = "Swift Code")
    @Column(name = "swift_code")
    private String swiftCode;

    @ApiModelProperty(value = "Iban")
    @Column(name = "iban")
    private String iban;

    @ApiModelProperty(value = "bank code")
    @Column(name = "bank_code")
    private String bankCode;

    @ApiModelProperty(value = "结算币种")
    @Column(name = "bank_currency")
    private String bankCurrency;

    @ApiModelProperty(value = "银行卡币种")
    @Column(name = "bankcode_currency")
    private String bankCodeCurrency;

    @ApiModelProperty(value = "结算状态")//结算状态：1-结算中 2-结算成功 3-结算失败
    @Column(name = "trade_status")
    private Byte tradeStatus;

    @ApiModelProperty(value = "结算类型")//结算类型：1-自动结算 2-手动结算
    @Column(name = "settle_type")
    private Byte settleType;

    @ApiModelProperty(value = "手续费")
    @Column(name = "fee")
    private BigDecimal fee;

    @ApiModelProperty(value = "批次交易手续费")
    @Column(name = "trade_fee")
    private BigDecimal tradeFee;

    @ApiModelProperty(value = "手续费币种")
    @Column(name = "fee_currency")
    private String feeCurrency;

    @ApiModelProperty(value = "交易汇率")
    @Column(name = "rate")
    private BigDecimal rate;

    @ApiModelProperty(value = "费率类型")
    @Column(name = "rate_type")
    private BigDecimal rateType;

    @ApiModelProperty(value = "费率")
    @Column(name = "fee_rate")
    private BigDecimal feeRate;

    @ApiModelProperty(value = "反向汇率")
    @Column(name = "reverse_rate")
    private BigDecimal reverseRate;

    @ApiModelProperty(value = "结算金额")//结算金额=交易金额*交易汇率
    @Column(name = "settle_amount")
    private BigDecimal settleAmount;

    @ApiModelProperty(value = "批次总结算金额")//总结算金额=交易金额*交易汇率-批次交易手续费
    @Column(name = "total_settle_amount")
    private BigDecimal totalSettleAmount;

    @ApiModelProperty(value = "结算通道")
    @Column(name = "settle_channel")
    private String settleChannel;

    @ApiModelProperty(value = "中间行银行编码")
    @Column(name = "intermediary_bank_code")
    private String intermediaryBankCode;

    @ApiModelProperty(value = "中间行银行名称")
    @Column(name = "intermediary_bank_name")
    private String intermediaryBankName;

    @ApiModelProperty(value = "中间行银行地址")
    @Column(name = "intermediary_bank_address")
    private String intermediaryBankAddress;

    @ApiModelProperty(value = "中间行银行账户")
    @Column(name = "intermediary_bank_account_no")
    private String intermediaryBankAccountNo;

    @ApiModelProperty(value = "中间行银行城市")
    @Column(name = "intermediary_bank_country")
    private String intermediaryBankCountry;

    @ApiModelProperty(value = "中间行银行其他code")
    @Column(name = "intermediary_other_code")
    private String intermediaryOtherCode;

    @ApiModelProperty(value = "机构地址")
    @Column(name = "institution_adress")
    private String institutionAdress;

    @ApiModelProperty(value = "机构邮政")
    @Column(name = "institution_postal_code")
    private String institutionPostalCode;

    @ApiModelProperty(value = "收款人地址")
    @Column(name = "receiver_address")
    private String receiverAddress;

    @ApiModelProperty(value = "备注1")
    @Column(name = "extend1")
    private String extend1;

    @ApiModelProperty(value = "备注2")
    @Column(name = "extend2")
    private String extend2;

    @ApiModelProperty(value = "备注3")
    @Column(name = "extend3")
    private String extend3;

    @ApiModelProperty(value = "备注4")
    @Column(name = "extend4")
    private String extend4;

}