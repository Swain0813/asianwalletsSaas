package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-25 15:52
 **/
@Data
@ApiModel(value = "银行卡实体", description = "银行卡实体")
public class BankCardDTO {

    @ApiModelProperty(value = "银行卡id")
    private String bankCardId;

    @ApiModelProperty(value = "机构code")
    private String institutionId;

    @ApiModelProperty(value = "账户编号")
    private String accountCode;

    @ApiModelProperty(value = "开户账号")
    private String bankAccountCode;

    @ApiModelProperty(value = "开户名称")
    private String accountName;

    @ApiModelProperty(value = "开户行名称")
    private String bankName;

    @ApiModelProperty(value = "swiftCode")
    private String swiftCode;

    @ApiModelProperty(value = "结算币种")
    private String settleCurrency;

    @ApiModelProperty(value = "银行卡币种")
    private String bankCurrency;

    @ApiModelProperty(value = "bankCode")
    private String bankCode;

    @ApiModelProperty(value = "银行卡账单地址")
    private String bankCardBillingAddress;

    @ApiModelProperty(value = "开户行地址")
    private String bankAddress;

    @ApiModelProperty(value = "性质 1-对公 2对私")
    private Byte nature;

    @ApiModelProperty(value = "收款人地区国家")
    private String receiver_country;

    @ApiModelProperty(value = "收款人地址")
    private String receiverAddress;

    @ApiModelProperty(value = "收款人")
    private String receiver;

    @ApiModelProperty(value = "iban")
    private String iban;

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
}
