package com.asianwallets.common.dto.ad3;

import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.DateToolUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;

@Data
@ApiModel(value = "AD3线下BSC支付接口业务参数实体", description = "AD3线下BSC支付接口业务参数实体")
public class BSCScanBizContentDTO {

    @ApiModelProperty(value = "终端编号")
    private String terminalId;

    @ApiModelProperty(value = "操作员ID")
    private String operatorId;

    @ApiModelProperty(value = "订单号")
    private String merOrderNo;

    @ApiModelProperty(value = "订单时间")// 固定14位 格式yyyyMMddHHmmss
    private String merorderDatetime;

    @ApiModelProperty(value = "订单币种")
    private String merorderCurrency;

    @ApiModelProperty(value = "订单金额")//保留二位小数
    private String merorderAmount;

    @ApiModelProperty(value = "交易金额 ")//保留二位小数
    private String payAmount;

    @ApiModelProperty(value = "付款人姓名")
    private String payerName;

    @ApiModelProperty(value = "业务类型")//1人名币业务 2跨境业务
    private String businessType;

    @ApiModelProperty(value = "支付方式")//35微信条码，37支付宝条码
    private String payType;

    @ApiModelProperty(value = "用户付款码")//payType为35或37时必填
    private String authCode;

    @ApiModelProperty(value = "银行机构号")//微信填写wechat支付宝填写alipay
    private String issuerId;

    @ApiModelProperty(value = "是否需要回调")
    private String isCallBack;

    @ApiModelProperty(value = "回调地址")
    private String receiveUrl;

    @ApiModelProperty(value = "商品或支付单简要描述")
    private String body;

    @ApiModelProperty(value = "备注1")
    private String ext1;

    @ApiModelProperty(value = "备注2")
    private String ext2;

    @ApiModelProperty(value = "备注3")
    private String ext3;

    public BSCScanBizContentDTO() {
    }

    public BSCScanBizContentDTO(Orders orders, String authCode, Channel channel) {
        this.terminalId = channel.getExtend1();//终端编号
        this.operatorId = channel.getExtend2();//操作员id
        this.merOrderNo = orders.getId();//订单id
        this.merorderDatetime = DateToolUtils.toString(new Date(), "yyyyMMddHHmmss");//订单时间
        this.merorderCurrency = orders.getTradeCurrency();//交易币种
        this.merorderAmount = String.valueOf(orders.getChannelAmount());//交易金额
        this.payAmount = String.valueOf(orders.getChannelAmount());//交易金额
        this.payerName = StringUtils.isEmpty(orders.getPayerName()) ? "" : orders.getPayerName();//付款人姓名
        this.businessType = AD3Constant.BUSINESS_OUT;//业务类型,境外
        this.payType = channel.getPayCode();//支付编码
        this.issuerId = channel.getIssuerId();//银行机构代码
        this.authCode = authCode;//用户付款码
        this.body = StringUtils.isEmpty(orders.getProductDescription()) ? "商品" : orders.getProductDescription();//商品描述
        this.ext1 = StringUtils.isEmpty(orders.getRemark1()) ? "" : orders.getRemark1();//备注1
        this.ext2 = StringUtils.isEmpty(orders.getRemark2()) ? "" : orders.getRemark2();//备注2
        this.ext3 = StringUtils.isEmpty(orders.getRemark3()) ? "" : orders.getRemark3();//备注3
        this.isCallBack = AD3Constant.NO_NEED_CALLBACK;//是否需要回调,否
        this.receiveUrl = "http://www.test.com";
    }
}
