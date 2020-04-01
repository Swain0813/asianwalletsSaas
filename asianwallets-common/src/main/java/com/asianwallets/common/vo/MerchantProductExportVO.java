package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 商户产品导出参数
 */
@Data
public class MerchantProductExportVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "产品名称")
    private String productAbbrev;

    @ApiModelProperty(value = "交易类型")
    private Integer tradeDirection;

    @ApiModelProperty(value = "结算周期")
    private String settleCycle;

    @ApiModelProperty(value = "费率类型")
    private String rateType;

    @ApiModelProperty(value = "费率")
    private BigDecimal rate;

    @ApiModelProperty(value = "费率最小值")
    private BigDecimal minTate;

    @ApiModelProperty(value = "费率最大值")
    private BigDecimal maxTate;

    @ApiModelProperty(value = "附加值")
    private BigDecimal addValue;

    @ApiModelProperty(value = "浮动率")
    private BigDecimal floatRate;

    @ApiModelProperty(value = "分润比例")
    private BigDecimal dividedRatio;

    @ApiModelProperty(value = "手续费承担方")//1-商家 2-用户
    private Byte feePayer;

    @ApiModelProperty(value = "退款是否收费")//1-是 0-否
    private Boolean refundDefault;

    @ApiModelProperty(value = "退款费率类型")
    private String refundRateType;

    @ApiModelProperty(value = "退款手续费最小值")
    private BigDecimal refundMinTate;

    @ApiModelProperty(value = "退款手续费最大值")
    private BigDecimal refundMaxTate;

    @ApiModelProperty(value = "退款费率")//dic_7_1-单笔费率,dic_7_2-单笔定额
    private BigDecimal refundRate;

    @ApiModelProperty(value = "退款附加值")
    private BigDecimal refundAddValue;

    @ApiModelProperty(value = "状态")
    private Boolean enabled;

    @ApiModelProperty(value = "生效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectTime;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

}
