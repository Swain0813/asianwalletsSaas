package com.asianwallets.trade.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author XuWenQi
 * @Date: 2019/3/7 16:58
 * @Description: 资金变动接口输出实体
 */
@Data
@ApiModel(value = "资金变动接口输出实体", description = "资金变动接口输出实体")
public class FundChangeVO {

    @ApiModelProperty(value = "返回结果")//T000表示成功，其余见响应码列表
    private String respCode;

    @ApiModelProperty(value = "返回结果描述")//success,成功或者错误提示
    private String respMsg;

    @ApiModelProperty(value = "版本号")//固定v1.0
    private String version;

    @ApiModelProperty(value = "字符集")//1.utf-8 2.gbk
    private String inputCharset;

    @ApiModelProperty(value = "语言")//1中文 2英文
    private String language;

    @ApiModelProperty(value = "商户号")
    private String merchantId;

    @ApiModelProperty(value = "是否清算")//清结算类型，1：清算，2结算
    private Integer isclear;

    @ApiModelProperty(value = "参考业务流水号")//所属业务表,eg:退款表记录flow,提款表记录flow
    private String refcnceFlow;

    @ApiModelProperty(value = "交易类型")//交易类型，NT：收单，RF：退款，RV：撤销，WD：退款，
    private String tradetype;

    @ApiModelProperty(value = "商户订单号")
    private String merOrderNo;

    @ApiModelProperty(value = "交易币种")//3位大写
    private String txncurrency;

    @ApiModelProperty(value = "交易金额")//小数保留2位
    private String txnamount;

    @ApiModelProperty(value = "应结日期")//yyyy-MM-dd HH:mm:ss
    private String shouldDealtime;

    @ApiModelProperty(value = "系统订单号")//交易订单号
    private String sysorderid;

    @ApiModelProperty(value = "手续费")//小数保留2位
    private String fee;

    @ApiModelProperty(value = "通道成本")//小数保留2位
    private String channelCost;

    @ApiModelProperty(value = "资金类型")//1：正常资金，2：冻结资金
    private String balancetype;

    @ApiModelProperty(value = "交易描述")
    private String txndesc;

    @ApiModelProperty(value = "交易汇率")
    private String txnexrate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "结算币种")
    private String sltcurrency;

    @ApiModelProperty(value = "结算金额")
    private String sltamount;

    @ApiModelProperty(value = "手续费币种")
    private String feecurrency;

    @ApiModelProperty(value = "通道成本币种")
    private String channelCostcurrency;

    @ApiModelProperty(value = "网关手续费")
    private String gatewayFee;

    @ApiModelProperty(value = "签名字符串")
    private String signMsg;
}
