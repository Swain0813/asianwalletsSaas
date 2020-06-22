package com.asianwallets.common.entity;
import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel(value = "通道", description = "通道")
public class Channel extends BaseEntity {

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "通道英文名称")
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

    @ApiModelProperty(value = "银行机构号")
    @Column(name = "issuer_id")
    private String issuerId;

    @ApiModelProperty(value = "结算周期")
    @Column(name = "settle_cycle")
    private String settleCycle;

    @ApiModelProperty(value = "通道服务名称标识")
    @Column(name = "service_name_mark")
    private String serviceNameMark;

    @ApiModelProperty(value = "服务器回调地址")
    @Column(name = "notify_server_url")
    private String notifyServerUrl;

    @ApiModelProperty(value = "浏览器回调地址")
    @Column(name = "notify_browser_url")
    private String notifyBrowserUrl;

    @ApiModelProperty(value = "支付url")
    @Column(name = "pay_url")
    private String payUrl;

    @ApiModelProperty(value = "撤销url")
    @Column(name = "void_url")
    private String voidUrl;

    @ApiModelProperty(value = "退款url")
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
    private BigDecimal limitMinAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "通道最大限额")
    @Column(name = "limit_max_amount")
    private BigDecimal limitMaxAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "通道费率")
    @Column(name = "channel_rate")
    private BigDecimal channelRate;

    @ApiModelProperty(value = "通道费率最小值")
    @Column(name = "channel_min_rate")
    private BigDecimal channelMinRate;

    @ApiModelProperty(value = "通道费率最大值")
    @Column(name = "channel_max_rate")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal channelMaxRate;

    @ApiModelProperty(value = "通道手续费类型")
    @Column(name = "channel_fee_type")
    private String channelFeeType;

    @ApiModelProperty(value = "通道网关费率")
    @Column(name = "channel_gateway_rate")
    private BigDecimal channelGatewayRate;

    @ApiModelProperty(value = "通道网关费率最小值")
    @Column(name = "channel_gateway_min_rate")
    private BigDecimal channelGatewayMinRate;

    @ApiModelProperty(value = "通道网关费率最大值")
    @Column(name = "channel_gateway_max_rate")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal channelGatewayMaxRate;

    @ApiModelProperty(value = "通道网关手续费类型")
    @Column(name = "channel_gateway_fee_type")
    private String channelGatewayFeeType;

    @ApiModelProperty(value = "支付编码")
    @Column(name = "pay_code")
    private String payCode;

    @ApiModelProperty(value = "通道网关是否收取 1-收 2-不收")
    @Column(name = "channel_gateway_charge")
    private Byte channelGatewayCharge;

    @ApiModelProperty(value = "通道网关收取状态 1-成功时收取 2-失败时收取 3-全收")
    @Column(name = "channel_gateway_status")
    private Byte channelGatewayStatus;

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

    @ApiModelProperty(value = "设备编号")
    @Column(name = "extend1")
    private String extend1;

    @ApiModelProperty(value = "操作员id")
    @Column(name = "extend2")
    private String extend2;

    @ApiModelProperty(value = "登录密码")
    @Column(name = "extend3")
    private String extend3;

    @ApiModelProperty(value = "交易密码")
    @Column(name = "extend4")
    private String extend4;

    @ApiModelProperty(value = "扩展字段5")
    @Column(name = "extend5")
    private String extend5;

    @ApiModelProperty(value = "扩展字段6")
    @Column(name = "extend6")
    private String extend6;

    @ApiModelProperty(value = "扩展字段7")
    @Column(name = "extend7")
    private String extend7;

    @ApiModelProperty(value = "扩展字段8")
    @Column(name = "extend8")
    private String extend8;

    @ApiModelProperty(value = "扩展字段9")
    @Column(name = "extend9")
    private String extend9;

    @ApiModelProperty(value = "扩展字段10")
    @Column(name = "extend10")
    private String extend10;

    @ApiModelProperty(value = "是否报备")
    @Column(name = "is_report")
    private Boolean isReport;

    @ApiModelProperty(value = "报备形式(1-接口报备 2-人工报备)")
    @Column(name = "report_form")
    private Byte reportForm;

    @ApiModelProperty(value = "是否退还收单手续费(1-退还,2-不退还,3-仅限当日退还)")
    @Column(name = "refunding_is_return_fee")
    private Byte refundingIsReturnFee;

    @ApiModelProperty(value = "通道代理商id")
    @Column(name = "channel_agent_id")
    private String channelAgentId;

    @ApiModelProperty(value = "通道退款手续费费率类型")
    @Column(name = "channel_refund_fee_type")
    private String channelRefundFeeType;

    @ApiModelProperty(value = "通道退款手续费费率")
    @Column(name = "channel_refund_fee_rate")
    private BigDecimal channelRefundFeeRate;

    @ApiModelProperty(value = "通道退款手续费最大值")
    @Column(name = "channel_refund_max_rate")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal channelRefundMaxRate;

    @ApiModelProperty(value = "通道退款手续费最小值")
    @Column(name = "channel_refund_min_rate")
    private BigDecimal channelRefundMinRate;

    @ApiModelProperty(value = "订单有效期")
    @Column(name = "order_validity")
    private Integer orderValidity;

    @ApiModelProperty(value = "仅当日交易可退款")//0-否,1-是
    @Column(name = "only_today_order_refund")
    private Boolean onlyTodayOrderRefund;


    @ApiModelProperty(value = "是否通道结算")
    @Column(name = "channel_support_settle")
    private Boolean channelSupportSettle;
}