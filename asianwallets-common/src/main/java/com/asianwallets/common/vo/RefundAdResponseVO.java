package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: AD3退款请求字段
 * @author: YangXu
 * @create: 2019-02-28 16:07
 **/
@Data
public class RefundAdResponseVO {
    @ApiModelProperty(value = "版本号固定值")
    private String version = "v1.0";

    @ApiModelProperty(value = "")
    private String inputCharset = "1";//1代表UTF-8、2代表GBK、3代表GB2312

    @ApiModelProperty(value = "")
    private String language = "1";//1代表简体中文、2代表繁体中文、3代表英文

    @ApiModelProperty(value = "商户的唯一编号")
    private String merchantId;

    @ApiModelProperty(value = "商户上送的商户订单号")
    private String merOrderNo;

    @ApiModelProperty(value = " 退款金额")
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = " 商户退款申请的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refundOrdertime;

    @ApiModelProperty(value = "退款状态 1-退款成功 2-退款失败")
    private String status;

    @ApiModelProperty(value = "ALLDEBIT返回的该笔退款订单的唯一流水号")
    private String txnId;

    @ApiModelProperty(value = "ALLDEBIT返回的退款的实际处理时间")
    private Date txnDate;

    @ApiModelProperty(value = "响应码。T000表示成功，其余见响应码列表")
    private String respCode ;

    @ApiModelProperty(value = "响应信息")
    private String respMsg  ;

    @ApiModelProperty(value = " 商户签名")
    private String signMsg="ede18ee2fe0171dd0d45be9cc257a54f";


}
