package com.asianwallets.common.dto.alipay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 支付宝码牌请求实体
 * @author: YangXu
 * @create: 2020年6月30日
 **/
@Data
@ApiModel(value = "支付宝码牌请求实体", description = "支付宝码牌请求实体")
public class AliPayCardDTO {

    @ApiModelProperty(value = "接口名称")
    private String service;

    @ApiModelProperty(value = "支付宝分配的用于标识支付宝帐户的合作伙伴ID")
    private String partner;

    @ApiModelProperty(value = "请求数据编码所用的字符集")
    private String _input_charset;

    @ApiModelProperty(value = " 标志类型。支持RSA，RSA2和MD5。使用大写。")
    private String sign_type;

    @ApiModelProperty(value = "GMT+8, 格式 yyyy-MM-dd HH:mm:ss.")
    private String timestamp;

    @ApiModelProperty(value = "支付宝产品的产品代码 OVERSEAS_MBARCODE_PAY")
    private String product_code;

    @ApiModelProperty(value = "商家在合同中指定的结算货币。使用大写")
    private String currency;

    @ApiModelProperty(value = "定价货币。为此参数和currency参数使用相同的值。")
    private String trans_currency;

    @ApiModelProperty(value = "交易流水号 自己")
    private String out_trade_no;

    @ApiModelProperty(value = "交易的简要说明")
    private String subject;

    @ApiModelProperty(value = "以trans_currency表示的交易总额")
    private String total_fee;

    @ApiModelProperty(value = "对应于卖家的支付宝帐户的唯一支付宝用户ID，包含16位数字")
    private String seller_id;

    @ApiModelProperty(value = "请求的扩展参数，并传输商人的商业信息")
    private String extend_params;

    @ApiModelProperty(value = "异步通知地址")
    private String notify_url;

    @ApiModelProperty(value = "签名值")
    private String sign;

    //#######以下是extend_params内的参数值 需要组装成JSON格式

    @ApiModelProperty(value = "合作伙伴分配的唯一ID，用于标识辅助商家。ID可以包含字母，数字和下划线。")
    private String secondary_merchant_id;

    @ApiModelProperty(value = " 辅助商家的注册法定名称，显示在“支付宝钱包”和对帐文件中以标识辅助商家")
    private String secondary_merchant_name;

    @ApiModelProperty(value = "次要商家的行业分类标识符，由支付宝分配")
    private String secondary_merchant_industry;

    @ApiModelProperty(value = "商店名称")
    private String store_name;

    @ApiModelProperty(value = "合作伙伴分配的用于标识商人商店的唯一ID")
    private String store_id;

}
