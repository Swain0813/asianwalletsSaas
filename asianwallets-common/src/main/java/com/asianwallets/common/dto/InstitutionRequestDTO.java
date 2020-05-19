package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "机构请求参数设置输入参数的实体", description = "机构请求参数设置输入参数的实体")
public class InstitutionRequestDTO extends BasePageHelper {

    @ApiModelProperty(value = "机构请求参数设置id")
    private String id;

    @ApiModelProperty(value = "机构编号")
    private String institutionCode;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "交易方向")//交易方向：1-线上 2-线下
    private Byte tradeDirection;

    @ApiModelProperty(value = "商户编号")
    private Boolean merchantId;


    @ApiModelProperty(value = "订单币种")
    private Boolean orderCurrency;

    @ApiModelProperty(value = "订单请求时间")
    private Boolean orderTime;

    @ApiModelProperty(value = "商户订单号")
    private Boolean orderNo;

    @ApiModelProperty(value = "订单金额")
    private Boolean orderAmount;

    @ApiModelProperty(value = "产品编号")
    private Boolean productCode;

    @ApiModelProperty(value = "设备编号")
    private Boolean imei;

    @ApiModelProperty(value = "操作员ID")
    private Boolean operatorId;

    @ApiModelProperty(value = "token")
    private Boolean token;

    @ApiModelProperty(value = "issuerId")
    private Boolean issuerId;

    @ApiModelProperty(value = "浏览器返回地址")
    private Boolean browserUrl;

    @ApiModelProperty(value = "服务器回调地址")
    private Boolean serverUrl;

    @ApiModelProperty(value = "商品名称")
    private Boolean productName;

    @ApiModelProperty(value = "商品描述")
    private Boolean productDescription;

    @ApiModelProperty(value = "付款人姓名")
    private Boolean payerName;

    @ApiModelProperty(value = "付款人电话")
    private Boolean payerPhone;

    @ApiModelProperty(value = "付款人邮箱")
    private Boolean payerEmail;

    @ApiModelProperty(value = "付款人银行")
    private Boolean payerBank;

    @ApiModelProperty(value = "付款码编号")
    private Boolean authCode;

    @ApiModelProperty(value = "签名类型")
    private Boolean signType;

    @ApiModelProperty(value = "签名")
    private Boolean sign;

    @ApiModelProperty(value = "语言")
    private Boolean language;

    @ApiModelProperty(value = "remark1")
    private Boolean remark1;

    @ApiModelProperty(value = "remark2")
    private Boolean remark2;

    @ApiModelProperty(value = "remark3")
    private Boolean remark3;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "起始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
