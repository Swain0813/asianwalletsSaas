package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * @description: 退款单请求参数
 * @author: YangXu
 * @create: 2019-02-19 09:49
 **/
@Data
@ApiModel(value = "退款单请求参数", description = "退款单请求参数")
public class RefundDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户订单号")
    private String orderNo;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "退款类型")//1：全额退款 2：部分退款
    private Byte refundType;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "退款时间")
    private String refundTime;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "退款币种")
    private String refundCurrency;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty(value = "交易方向")//1-线上 2-线下
    @NotNull(message = "50002")
    private Byte tradeDirection;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "签名类型")//1为RSA 2为MD5
    private String signType;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "签名")
    private String sign;

    //----------------【非必填参数】---------------------
    @ApiModelProperty(value = "付款人姓名")
    private String payerName;

    @ApiModelProperty(value = "付款人账户")
    private String payerAccount;

    @ApiModelProperty(value = "付款人银行")
    private String payerBank;

    @ApiModelProperty(value = "付款人邮箱")
    private String payerEmail;

    @ApiModelProperty(value = "付款人电话")
    private String payerPhone;

    @ApiModelProperty(value = "Swift Code")
    private String swiftCode;

    @ApiModelProperty(value = "设备编号")
    private String imei;

    @ApiModelProperty(value = "设备操作员")
    private String operatorId;

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "交易密码")
    private String tradePassword;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "更新者")
    private String modifier;

    @ApiModelProperty(value = "银行卡号")
    private String userBankCardNo;

    @ApiModelProperty(value = "cvv")
    private String cvv;

    @ApiModelProperty(value = "卡有效期")
    private String valid;

    @ApiModelProperty(value = "磁道信息")
    private String trackData;

    /**
     * 由于撤销和冲正功能依赖退款的方法，为了签名的验签在各自自己的方法做
     * 所以特意加个标志
     */
    @ApiModelProperty(value ="区分退款和撤销以及冲正功能的标志")
    private String functionType;


}
