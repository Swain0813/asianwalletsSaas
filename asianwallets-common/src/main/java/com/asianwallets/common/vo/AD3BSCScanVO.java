package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author XuWenQi
 * @Date: 2019/3/29 11:22
 * @Description: BSC扫码支付接口输出实体
 */
@Data
@ApiModel(value = "BSC扫码支付接口输出实体", description = "BSC扫码支付接口输出实体")
public class AD3BSCScanVO {

    @ApiModelProperty(value = "签名")//1中文 2英文
    private String signMsg;

    @ApiModelProperty(value = "返回结果")//T000表示成功，其余见响应码列表
    private String respCode;

    @ApiModelProperty(value = "返回结果描述")//success,成功或者错误提示
    private String respMsg;

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "终端编号")
    private String terminalId;

    @ApiModelProperty(value = "操作员ID")
    private String operatorId;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "字符集")//1.utf-8 2.gbk
    private String inputCharset;

    @ApiModelProperty(value = "语言")//1中文 2英文
    private String language;

    @ApiModelProperty(value = "订单号")
    private String merOrderNo;

    @ApiModelProperty(value = "订单时间")// 固定14位 格式yyyyMMddHHmmss
    private String merorderDatetime;

    @ApiModelProperty(value = "订单币种")
    private String merorderCurrency;

    @ApiModelProperty(value = "订单金额")
    private String merorderAmount;

    @ApiModelProperty(value = "业务类型")//1人名币业务 2跨境业务
    private String businessType;

    @ApiModelProperty(value = "支付方式")//35微信条码，37支付宝条码
    private String payType;

    @ApiModelProperty(value = "银行机构号")
    private String issuerId;

    @ApiModelProperty(value = "交易系统订单号")
    private String txnId;

    @ApiModelProperty(value = "支付完成时间")//yyyyMMDDhhmmss
    private String payFinishTime;

    @ApiModelProperty(value = "备注一")
    private String ext1;

    @ApiModelProperty(value = "备注二")
    private String ext2;

    @ApiModelProperty(value = "备注三")
    private String ext3;

}
