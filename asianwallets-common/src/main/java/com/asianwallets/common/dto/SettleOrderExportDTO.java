package com.asianwallets.common.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 后台结算表导出实体
 */
@Data
@ApiModel(value = "后台结算表导出实体", description = "后台结算表导出实体")
public class SettleOrderExportDTO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "交易币种")
    private String txncurrency;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal txnamount;

    @ApiModelProperty(value = "结算账户")
    private String accountCode;

    @ApiModelProperty(value = "账户名")
    private String accountName;

    @ApiModelProperty(value = "收款人地址")
    private String receiverAddress;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "Swift Code")
    private String swiftCode;

    @ApiModelProperty(value = "Iban")
    private String iban;

    @ApiModelProperty(value = "bank code")
    private String bankCode;

    @ApiModelProperty(value = "结算币种")
    private String bankCurrency;

    @ApiModelProperty(value = "银行卡币种")
    private String bankCodeCurrency;

    @ApiModelProperty(value = "结算状态")//交易状态：1-结算中 2-结算成功 3-结算失败
    private Byte tradeStatus;

    @ApiModelProperty(value = "批次交易手续费")
    private BigDecimal tradeFee;

    @ApiModelProperty(value = "手续费币种")
    private String feeCurrency;

    @ApiModelProperty(value = "交易汇率")
    private BigDecimal rate;

    @ApiModelProperty(value = "批次总结算金额")
    private BigDecimal totalSettleAmount;

    @ApiModelProperty(value = "结算通道")
    private String settleChannel;

    @ApiModelProperty(value = "中间行银行编码")
    private String intermediaryBankCode;

    @ApiModelProperty(value = "中间行银行名称")
    private String intermediaryBankName;

    @ApiModelProperty(value = "中间行银行地址")
    private String intermediaryBankAddress;

    @ApiModelProperty(value = "中间行银行账户")
    private String intermediaryBankAccountNo;

    @ApiModelProperty(value = "中间行银行城市")
    private String intermediaryBankCountry;

    @ApiModelProperty(value = "中间行银行其他code")
    private String intermediaryOtherCode;

    @ApiModelProperty(value = "机构地址")
    private String institutionAdress;

    @ApiModelProperty(value = "机构邮政")
    private String institutionPostalCode;

    @ApiModelProperty(value = "审核时间")
    public Date updateTime;

    @ApiModelProperty(value = "审核人")
    private String modifier;

}
