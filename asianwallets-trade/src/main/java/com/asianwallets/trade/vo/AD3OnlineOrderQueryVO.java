package com.asianwallets.trade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shenxinran
 * @Date: 2019/3/19 16:58
 * @Description: AD3线上订单查询响应实体
 */
@Data
@ApiModel(value = "AD3线上订单查询响应实体", description = "AD3线上订单查询响应实体")
public class AD3OnlineOrderQueryVO {

    @ApiModelProperty(value = "固定值v1.0")
    private String version = "v1.0";

    @ApiModelProperty(value = "字符集")
    private String inputCharset;

    @ApiModelProperty(value = "开放给商户的唯一编号")
    private String merchantId;

    @ApiModelProperty(value = "收单时商户上送的商户订单号")
    private String merOrderNo;

    @ApiModelProperty(value = "收单时商户上送的订单时间，格式yyyyMMddHHmmss")
    private String merorderDatetime;

    @ApiModelProperty(value = "订单币种")
    private String merorderCurrency;

    @ApiModelProperty(value = "订单金额，最多保留小数点后两位")
    private String merorderAmount;

    @ApiModelProperty(value = "备注信息1")
    private String ext1;

    @ApiModelProperty(value = "备注信息2")
    private String ext2;

    @ApiModelProperty(value = "备注信息3")
    private String ext3;

    @ApiModelProperty(value = "ALLDEBIT的系统流水号")
    private String txnId;

    @ApiModelProperty(value = "ALLDEBIT的系统处理时间yyyyMMddHHmmss")
    private String txnDate;

    @ApiModelProperty(value = "订单状态")
    private String state;

    @ApiModelProperty(value = "响应码")
    private String respCode;

    @ApiModelProperty(value = "响应信息")
    private String respMsg;

    @ApiModelProperty(value = "签名")
    private String signMsg;

}
