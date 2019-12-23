package com.asianwallets.common.dto.ad3;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.utils.DateToolUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: Ad3退款上报实体
 * @author: YangXu
 * @create: 2019-02-28 14:18
 **/
@Data
public class SendAdRefundDTO {

    @ApiModelProperty(value = "版本号固定值")
    private String version ;

    @ApiModelProperty(value = "字符集")
    private String inputCharset;//1代表UTF-8、2代表GBK、3代表GB2312

    @ApiModelProperty(value = "语言")
    private String language ;//1代表简体中文、2代表繁体中文、3代表英文

    @ApiModelProperty(value = "商户的唯一编号")
    private String merchantId ;

    @ApiModelProperty(value = "原交易商户上送的商户订单号")
    private String merOrderNo;

    @ApiModelProperty(value = "原交易成功时系统返回的订单唯一流水号")
    private String sysOrderNo;

    @ApiModelProperty(value = " 退款金额")
    private String refundAmount;

    @ApiModelProperty(value = "商户加密证书方式")
    private String merchantSignType ;

    @ApiModelProperty(value = " 商户退款申请的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String refundOrdertime;

    @ApiModelProperty(value = " 备注字段1")
    private String ext1;

    @ApiModelProperty(value = " 商户签名")
    private String signMsg;


    public SendAdRefundDTO() {
    }

    public SendAdRefundDTO(String merchantOnlineCode, OrderRefund orderRefund) {
        this.version = "v1.0";
        this.inputCharset = AD3Constant.CHARSET_UTF_8;
        this.language = AD3Constant.LANGUAGE_CN;
        this.merchantId = merchantOnlineCode;
        this.merOrderNo = orderRefund.getOrderId();
        this.sysOrderNo = orderRefund.getChannelNumber();
        if(orderRefund.getFeePayer()==1){
            this.refundAmount =  String.valueOf(orderRefund.getTradeAmount());
        }else{
            this.refundAmount =  String.valueOf(orderRefund.getTradeAmount().add(orderRefund.getRefundOrderFeeTrade()));
        }
        this.refundOrdertime = DateToolUtils.getReqDateyyyyMMddHHmmss(orderRefund.getCreateTime());
        this.ext1=orderRefund.getId();
        this.merchantSignType="2";
    }
}
