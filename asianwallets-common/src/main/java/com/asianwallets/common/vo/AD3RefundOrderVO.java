package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: AD3线下退款返回参数
 * @author: YangXu
 * @create: 2019-03-14 10:50
 **/
@Data
@ApiModel(value = "AD3线下退款返回参数", description = "AD3线下退款返回参数")
public class AD3RefundOrderVO {

    @ApiModelProperty(value = "密文")
    private String signMsg ;

    @ApiModelProperty(value = "返回结果")
    private String respCode ;

    @ApiModelProperty(value = "返回结果描述")
    private String respMsg ;

    @ApiModelProperty(value = "版本号")
    private String version ;

    @ApiModelProperty(value = "编码")
    private String inputCharset ;

    @ApiModelProperty(value = "语言")
    private String language ;

    @ApiModelProperty(value = "商户号")
    private String merchantId ;

    @ApiModelProperty(value = "终端编号")
    private String terminalId ;

    @ApiModelProperty(value = "操作员ID")
    private String operatorId ;

    @ApiModelProperty(value = "终端系统订单号")
    private String merOrderNo ;

    @ApiModelProperty(value = "终端提交过来的退款流水号")
    private String outRefundId ;

    @ApiModelProperty(value = "交易系统订单流水号")
    private String sysOrderNo ;

    @ApiModelProperty(value = "交易系统退款订单号")
    private String sysRefundId ;

    @ApiModelProperty(value = "退款金额")
    private String refundAmount ;

    @ApiModelProperty(value = "退款状态")
    private String status ;

}
