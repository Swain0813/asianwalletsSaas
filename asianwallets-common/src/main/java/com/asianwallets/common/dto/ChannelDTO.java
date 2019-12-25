package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "通道输入DTO", description = "通道输入DTO")
public class ChannelDTO extends BasePageHelper {

    @ApiModelProperty(value = "通道ID")
    private String channelId;

    @ApiModelProperty(value = "支付编码")
    private String payCode;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "通道英文名称")
    private String channelEnName;

    @ApiModelProperty(value = "通道中文名称")
    private String channelCnName;

    @ApiModelProperty(value = "通道图片")
    private String channelImg;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "国家类别")
    private Byte countryType;

    @ApiModelProperty(value = "交易类型")
    private Byte transType;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "银行机构号")
    private String issuerId;

    @ApiModelProperty(value = "结算周期")
    private String settleCycle;

    @ApiModelProperty(value = "服务器回调地址")
    private String notifyServerUrl;

    @ApiModelProperty(value = "浏览器回调地址")
    private String notifyBrowserUrl;

    @ApiModelProperty(value = "支付url")
    private String payUrl;

    @ApiModelProperty(value = "通道单个查询url")
    private String channelSingleSelectUrl;

    @ApiModelProperty(value = "撤销url")
    private String voidUrl;

    @ApiModelProperty(value = "退款url")
    private String refundUrl;

    @ApiModelProperty(value = "通道最小限额")
    private BigDecimal limitMinAmount;

    @ApiModelProperty(value = "通道最大限额")
    private BigDecimal limitMaxAmount;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "是否支持退款")
    private Boolean supportRefundState;

    @ApiModelProperty(value = "是否直连")
    private Byte channelConnectMethod;

    @ApiModelProperty(value = "通道批量查询url")
    private String channelBatchSelectUrl;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "通道费率")
    private BigDecimal channelRate;

    @ApiModelProperty(value = "通道费率最小值")
    private BigDecimal channelMinRate;

    @ApiModelProperty(value = "通道费率最大值")
    private BigDecimal channelMaxRate;

    @ApiModelProperty(value = "通道手续费类型")
    private String channelFeeType;

    @ApiModelProperty(value = "通道网关费率")
    private BigDecimal channelGatewayRate;

    @ApiModelProperty(value = "通道网关费率最小值")
    private BigDecimal channelGatewayMinRate;

    @ApiModelProperty(value = "通道网关费率最大值")
    private BigDecimal channelGatewayMaxRate;

    @ApiModelProperty(value = "通道网关手续费类型 1-单笔费率,2-单笔定额")
    private String channelGatewayFeeType;

    @ApiModelProperty(value = "通道网关是否收取 1-收 2-不收")
    private Byte channelGatewayCharge;

    @ApiModelProperty(value = "通道网关收取状态  1-成功时收取 2-失败时收取 3-全收")
    private Byte channelGatewayStatus;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "通道商户号")
    private String channelMerchantId;

    @ApiModelProperty(value = "通道加密MD5key")
    private String md5KeyStr;

    @ApiModelProperty(value = "优先级")
    private String sort;

    @ApiModelProperty(value = "是否报备")
    private Boolean isReport;

    @ApiModelProperty(value = "报备形式(1-接口,2-人工)")
    private Byte reportForm;

    @ApiModelProperty(value = "退款时是否退还收单手续费(1-退还,2-不退还,3-仅限当日退还)")
    private Byte refundingIsReturnFee;

    @ApiModelProperty(value = "通道代理商id")
    private String channelAgentId;

    @ApiModelProperty(value = "通道退款手续费费率类型")
    private String channelRefundFeeType;

    @ApiModelProperty(value = "通道退款手续费费率")
    private BigDecimal channelRefundFeeRate;

    @ApiModelProperty(value = "通道退款手续费最大值")
    private BigDecimal channelRefundMaxRate;

    @ApiModelProperty(value = "通道退款手续费最小值")
    private BigDecimal channelRefundMinRate;

    @ApiModelProperty(value = "订单有效期")
    private Integer orderValidity;

    @ApiModelProperty(value = "仅当日交易可退款(0-否,1-是)")
    private Boolean onlyTodayOrderRefund;

    @ApiModelProperty(value = "扩展字段1")
    private String extend1;

    @ApiModelProperty(value = "扩展字段2")
    private String extend2;

    @ApiModelProperty(value = "扩展字段3")
    private String extend3;

    @ApiModelProperty(value = "扩展字段4")
    private String extend4;

    @ApiModelProperty(value = "扩展字段5")
    private String extend5;

    @ApiModelProperty(value = "扩展字段6")
    private String extend6;

    @ApiModelProperty(value = "扩展字段7")
    private String extend7;

    @ApiModelProperty(value = "扩展字段8")
    private String extend8;

    @ApiModelProperty(value = "扩展字段9")
    private String extend9;

    @ApiModelProperty(value = "扩展字段10")
    private String extend10;

    @ApiModelProperty(value = "通道服务名称标识")
    private String serviceNameMark;

    @ApiModelProperty(value = "产品ID集合")
    private List<String> productIdList;

    @ApiModelProperty(value = "银行ID集合")
    private List<String> bankIdList;

}
