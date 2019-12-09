package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "channel")
@ApiModel(value = "通道配置", description = "通道配置")
public class Channel extends BaseEntity {

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "通道的服务名称比如AD3_ONLINE,AD3_OFFLINE")
    @Column(name = "channel_en_name")
    private String channelEnName;

    @ApiModelProperty(value = "通道中文名称")
    @Column(name = "channel_cn_name")
    private String channelCnName;

    @ApiModelProperty(value = "通道图片")
    @Column(name = "channel_img")
    private String channelImg;

    @ApiModelProperty(value = "国家")
    @Column(name = "country")
    private String country;

    @ApiModelProperty(value = "国家类别")
    @Column(name = "country_type")
    private Byte countryType;

    @ApiModelProperty(value = "交易类型")
    @Column(name = "trans_type")
    private Byte transType;

    @ApiModelProperty(value = "是否直连")
    @Column(name = "channel_connect_method")
    private Byte channelConnectMethod;

    @ApiModelProperty(value = "支付方式")
    @Column(name = "pay_type")
    private String payType;

    @ApiModelProperty(value = "币种")
    @Column(name = "currency")
    private String currency;

    @ApiModelProperty(value = "银行机构号：微信填写wechat支付宝填写alipay")
    @Column(name = "issuer_id")
    private String issuerId;

    @ApiModelProperty(value = "结算周期")
    @Column(name = "settle_cycle")
    private String settleCycle;

    @ApiModelProperty(value = "通道url")
    @Column(name = "channel_url")
    private String channelUrl;

    @ApiModelProperty(value = "Swift Code")//Swift Code
    @Column(name = "refund_url")
    private String refundUrl;

    @ApiModelProperty(value = "通道单个查询url")
    @Column(name = "channel_single_select_url")
    private String channelSingleSelectUrl;

    @ApiModelProperty(value = "通道批量查询url")
    @Column(name = "channel_batch_select_url")
    private String channelBatchSelectUrl;

    @ApiModelProperty(value = "通道最小限额")
    @Column(name = "limit_min_amount")
    private BigDecimal limitMinAmount;

    @ApiModelProperty(value = "通道最大限额")
    @Column(name = "limit_max_amount")
    private BigDecimal limitMaxAmount;

    @ApiModelProperty(value = "通道费率")
    @Column(name = "channel_rate")
    private BigDecimal channelRate;

    @ApiModelProperty(value = "通道手续费")
    @Column(name = "channel_fee")
    private BigDecimal channelFee;

    @ApiModelProperty(value = "通道费率最小值")
    @Column(name = "channel_min_rate")
    private BigDecimal channelMinRate;

    @ApiModelProperty(value = "通道费率最大值")
    @Column(name = "channel_max_rate")
    private BigDecimal channelMaxRate;

    @ApiModelProperty(value = "通道手续费类型")
    @Column(name = "channel_fee_type")
    private String channelFeeType;

    @ApiModelProperty(value = "通道网关费率")
    @Column(name = "channel_gateway_rate")
    private BigDecimal channelGatewayRate;

    @ApiModelProperty(value = "通道网关手续费")
    @Column(name = "channel_gateway_fee")
    private BigDecimal channelGatewayFee;

    @ApiModelProperty(value = "通道网关费率最小值")
    @Column(name = "channel_gateway_min_rate")
    private BigDecimal channelGatewayMinRate;

    @ApiModelProperty(value = "通道网关费率最大值")
    @Column(name = "channel_gateway_max_rate")
    private BigDecimal channelGatewayMaxRate;

    @ApiModelProperty(value = "通道网关手续费类型")
    @Column(name = "channel_gateway_fee_type")
    private String channelGatewayFeeType;//1-单笔费率,2-单笔定额

    @ApiModelProperty(value = "ad3的支付code")
    @Column(name = "pay_code")
    private String payCode;//ad3的支付code

    @ApiModelProperty(value = "通道网关是否收取")
    @Column(name = "channel_gateway_charge")
    private Byte channelGatewayCharge;//1-收 2-不收

    @ApiModelProperty(value = "通道网关收取状态")
    @Column(name = "channel_gateway_status")
    private Byte channelGatewayStatus;// 1-成功时收取 2-失败时收取 3-全收

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "是否支持退款")
    @Column(name = "support_refund_state")
    private Boolean supportRefundState;

    @ApiModelProperty(value = "通道商户号")
    @Column(name = "channel_merchant_id")
    private String channelMerchantId;

    @ApiModelProperty(value = "权重")
    @Column(name = "sort")
    private String sort;

    @ApiModelProperty(value = "通道加密MD5key")
    @Column(name = "md5_key_str")
    private String md5KeyStr;
}