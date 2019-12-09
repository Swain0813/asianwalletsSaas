package com.asianwallets.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-09 14:13
 **/
@Data
@ApiModel(value = "商户产品实体", description = "商户产品实体")
public class MerchantProductDTO {

    @ApiModelProperty(value = "商户id")
    private String merchantId;
    @ApiModelProperty(value = "产品id")
    private String productId;
    @ApiModelProperty(value = "商户名称")
    private String merchantName;
    @ApiModelProperty(value = "商品简称")
    private String productAbbrev;
    @ApiModelProperty(value = "交易场景：1-线上 2-线下")
    private Integer tradeDirection;
    @ApiModelProperty(value = "交易类型（1-收、2-付）")
    private Integer transType;
    @ApiModelProperty(value = "支付方式(银联，网银，...）")
    private String payType;
    @ApiModelProperty(value = "费率类型 (1-单笔费率,2-单笔定额)")
    private String rateType;
    @ApiModelProperty(value = "费率最小值")
    private BigDecimal minTate;
    @ApiModelProperty(value = "费率最大值")
    private BigDecimal maxTate;
    @ApiModelProperty(value = "费率")
    private BigDecimal rate;
    @ApiModelProperty(value = "附加值")
    private BigDecimal addValue;
    @ApiModelProperty(value = "单笔交易限额")
    private BigDecimal limitAmount;
    @ApiModelProperty(value = "日累计交易笔数")
    private Integer dailyTradingCount;
    @ApiModelProperty(value = "浮动率")
    private BigDecimal floatRate;
    @ApiModelProperty(value = "分润比例")
    private BigDecimal dividedRatio;
    @ApiModelProperty(value = "分润模式 1-分成 2-费用差")
    private Integer dividedMode;
    @ApiModelProperty(value = "手续费付款方 1 ：内扣 2 ：外扣")
    private Integer feePayer;
    @ApiModelProperty(value = "退款是否收费")
    private Boolean refundDefault;
    @ApiModelProperty(value = "退款费率类型")
    private String refundRateType;
    @ApiModelProperty(value = "退款手续费最小值")
    private BigDecimal refundMinTate;
    @ApiModelProperty(value = "退款手续费最大值")
    private BigDecimal refundMaxTate;
    @ApiModelProperty(value = "退款费率")
    private BigDecimal refundRate;
    @ApiModelProperty(value = "退款附加值")
    private BigDecimal refundAddValue;
    @ApiModelProperty(value = "结算周期 (T+0,T+3,...)")
    private String settleCycle;
    @ApiModelProperty(value = "审核状态 1-待审核 2-审核通过 3-审核不通过")
    private Integer auditStatus;
    @ApiModelProperty(value = "审核备注")
    private String auditRemark;
    @ApiModelProperty(value = "enabled")
    private Boolean enabled;
    @ApiModelProperty(value = "生效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String effectTime;

    @ApiModelProperty(value = "ext1")
    private String ext1;
    @ApiModelProperty(value = "ext2")
    private String ext2;
    @ApiModelProperty(value = "ext3")
    private String ext3;
    @ApiModelProperty(value = "ext4")
    @Column(name = "ext4")
    private String ext4;
    @ApiModelProperty(value = "ext5")
    private String ext5;
    @ApiModelProperty(value = "ext6")
    private String ext6;
    @ApiModelProperty(value = "ext7")
    private String ext7;


}
