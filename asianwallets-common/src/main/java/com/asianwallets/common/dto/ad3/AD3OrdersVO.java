package com.asianwallets.common.dto.ad3;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author XuWenQi
 * @Date: 2019/3/5 11:43
 * @Description: AD3订单输出实体
 */
@Data
@ApiModel(value = "AD3订单输出实体", description = "AD3订单输出实体")
public class AD3OrdersVO {

    @ApiModelProperty(value = "签名")
    private String signMsg;

    @ApiModelProperty(value = "返回结果")//T000表示成功，其余见响应码列表
    private String respCode;

    @ApiModelProperty(value = "返回结果描述")//success,成功或者错误提示
    private String respMsg;

    @ApiModelProperty(value = "版本号")
    private String version ;

    @ApiModelProperty(value = "字符集")//1.utf-8 2.gbk
    private String inputCharset;

    @ApiModelProperty(value = "语言")//1中文 2英文
    private String language;

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "终端编号")
    private String terminalId;

    @ApiModelProperty(value = "操作员ID")
    private String operatorId;

    @ApiModelProperty(value = "订单号")
    private String merOrderNo;

    @ApiModelProperty(value = "业务类型")//1人名币业务 2跨境业务
    private String bussinesType;

    @ApiModelProperty(value = "订单时间")// 固定14位 格式yyyyMMddHHmmss
    private String merorderDatetime;

    @ApiModelProperty(value = "所属机构编号")//
    private String organId;

    @ApiModelProperty(value = "订单币种")
    private String merorderCurrency;

    @ApiModelProperty(value = "订单金额")//保留二位小数
    private String merorderAmount;

    @ApiModelProperty(value = "备注1")
    private String ext1;

    @ApiModelProperty(value = "备注2")
    private String ext2;

    @ApiModelProperty(value = "备注3")
    private String ext3;

    @ApiModelProperty(value = "平台系统流水号")
    private String txnId;

    @ApiModelProperty(value = "平台处理时间")
    private String txnDate;

    //支付订单状态：1.交易中   2支付失败 3.支付成功
    //退款订单状态：1.受理成功 2退款失败 3.完成退款
    @ApiModelProperty(value = "支付订单状态")
    private String state;

    @ApiModelProperty(value = "查询类型")//1交易订单查询，2退款订单查询
    private Integer type;

    @ApiModelProperty(value = "商户名称")//商户名称
    private String merchantName;

    @ApiModelProperty(value = "头部logo图片存放地址URL")//头部logo图片存放地址URL
    private String topLogo;

    @ApiModelProperty(value = "尾部logo图片存放地址URL")//尾部logo图片存放地址URL
    private String bottomLogo;

    @ApiModelProperty(value = "商户退款订单编号")//商户退款订单编号，type=2时必填
    private String merchantRefundId;

    @ApiModelProperty(value = "退款状态")//退款状态 1标识冲正 其它标识退款
    private Integer refundType;

    @ApiModelProperty(value = "退款状态")//退款状态(仅在订单查询返回时有)1受理成功 2受理失败 3退款成功 4退款失败
    private Integer refundState;

    @ApiModelProperty(value = "支付类型")//支付类型
    private String payType;

    @ApiModelProperty(value = "银行渠道流水")//银行渠道流水
    private String bankNo;
}
