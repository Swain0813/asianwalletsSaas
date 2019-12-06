package com.asianwallets.common.entity;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 机构请求参数设置表
 */
@Data
@Entity
@Table(name =  "institution_request_parameters")
@ApiModel(value = "机构请求参数设置表", description = "机构请求参数设置表")
public class InstitutionRequestParameters extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_code")
    private String institutionCode;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "交易方向")//交易方向：1-线上 2-线下
    @Column(name = "trade_direction")
    private Byte tradeDirection;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private Boolean merchantId=Boolean.TRUE;


    @ApiModelProperty(value = "订单币种")
    @Column(name = "order_currency")
    private Boolean orderCurrency=Boolean.TRUE;

    @ApiModelProperty(value = "订单请求时间")
    @Column(name = "order_time")
    private Boolean orderTime=Boolean.TRUE;

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "order_no")
    private Boolean orderNo=Boolean.TRUE;

    @ApiModelProperty(value = "订单金额")
    @Column(name = "order_amount")
    private Boolean orderAmount=Boolean.TRUE;

    @ApiModelProperty(value = "产品编号")
    @Column(name = "product_code")
    private Boolean productCode;

    @ApiModelProperty(value = "设备编号")
    @Column(name = "imei")
    private Boolean imei;

    @ApiModelProperty(value = "操作员ID")
    @Column(name = "operator_id")
    private Boolean operatorId;

    @ApiModelProperty(value = "token")
    @Column(name = "token")
    private Boolean token;

    @ApiModelProperty(value = "issuerId")
    @Column(name = "issuer_id")
    private Boolean issuerId;

    @ApiModelProperty(value = "浏览器返回地址")
    @Column(name = "browser_url")
    private Boolean browserUrl;

    @ApiModelProperty(value = "服务器回调地址")
    @Column(name = "server_url")
    private Boolean serverUrl;

    @ApiModelProperty(value = "商品名称")
    @Column(name = "product_name")
    private Boolean productName;

    @ApiModelProperty(value = "商品描述")
    @Column(name = "product_description")
    private Boolean productDescription;

    @ApiModelProperty(value = "付款人姓名")
    @Column(name = "payer_name")
    private Boolean payerName;

    @ApiModelProperty(value = "付款人电话")
    @Column(name = "payer_phone")
    private Boolean payerPhone;

    @ApiModelProperty(value = "付款人邮箱")
    @Column(name = "payer_email")
    private Boolean payerEmail;

    @ApiModelProperty(value = "付款人银行")
    @Column(name = "payer_bank")
    private Boolean payerBank;

    @ApiModelProperty(value = "付款码编号")
    @Column(name = "auth_code")
    private Boolean authCode;

    @ApiModelProperty(value = "签名类型")
    @Column(name = "sign_type")
    private Boolean signType;

    @ApiModelProperty(value = "签名")
    @Column(name = "sign")
    private Boolean sign=Boolean.TRUE;

    @ApiModelProperty(value = "语言")
    @Column(name = "language")
    private Boolean language;

    @ApiModelProperty(value = "remark1")
    @Column(name = "remark1")
    private Boolean remark1;

    @ApiModelProperty(value = "remark2")
    @Column(name = "remark2")
    private Boolean remark2;

    @ApiModelProperty(value = "remark3")
    @Column(name = "remark3")
    private Boolean remark3;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;
}