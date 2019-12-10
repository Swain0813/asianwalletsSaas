package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-10 17:24
 **/
@Data
public class MerchantProductExportVO {

    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户编号")
    private String merchantId;
    /**
     * 商户名称
     */
    @ApiModelProperty(value = "商户名称")
    private String merchantName;
    /**
     * 交易场景：1-线上 2-线下
     */
    @ApiModelProperty(value = "交易场景")
    private Integer tradeDirection;
    /**
     * 商品简称
     */
    @ApiModelProperty(value = "产品名称")
    @Column(name ="product_abbrev")
    private String productAbbrev;
    ///**
    // * 交易类型（1-收、2-付）
    // */
    //@ApiModelProperty(value = "交易类型（1-收、2-付）")
    //@Column(name ="trans_type")
    //private Integer transType;
    ///**
    // * 支付方式(银联，网银，...）
    // */
    //@ApiModelProperty(value = "支付方式(银联，网银，...）")
    //@Column(name ="pay_type")
    //private String payType;
    /**
     * 费率类型 (1-单笔费率,2-单笔定额)
     */
    @ApiModelProperty(value = "费率类型 (1-单笔费率,2-单笔定额)")
    private String rateType;
    /**
     * 费率
     */
    @ApiModelProperty(value = "费率")
    @Column(name ="rate")
    private BigDecimal rate;
    /**
     * 费率最小值
     */
    @ApiModelProperty(value = "费率最小值")
    private BigDecimal minTate;
    /**
     * 费率最大值
     */
    @ApiModelProperty(value = "费率最大值")
    private BigDecimal maxTate;
    /**
     * 附加值
     */
    @ApiModelProperty(value = "附加值")
    private BigDecimal addValue;
    ///**
    // * 浮动率
    // */
    //@ApiModelProperty(value = "浮动率")
    //private BigDecimal floatRate;
    ///**
    // * 分润比例
    // */
    //@ApiModelProperty(value = "分润比例")
    //@Column(name ="divided_ratio")
    //private BigDecimal dividedRatio;
    ///**
    // * 分润模式 1-分成 2-费用差
    // */
    //@ApiModelProperty(value = "分润模式 1-分成 2-费用差")
    //@Column(name ="divided_mode")
    //private Integer dividedMode;
    ///**
    // * 手续费付款方 1 ：内扣 2 ：外扣
    // */
    //@ApiModelProperty(value = "手续费付款方 1 ：内扣 2 ：外扣")
    //@Column(name ="fee_payer")
    //private Integer feePayer;
    ///**
    // * 退款是否收费
    // */
    //@ApiModelProperty(value = "退款是否收费")
    //@Column(name ="refund_default")
    //private Boolean refundDefault;
    ///**
    // * 退款费率类型
    // */
    //@ApiModelProperty(value = "退款费率类型")
    //@Column(name ="refund_rate_type")
    //private String refundRateType;
    ///**
    // * 退款手续费最小值
    // */
    //@ApiModelProperty(value = "退款手续费最小值")
    //@Column(name ="refund_min_tate")
    //private BigDecimal refundMinTate;
    ///**
    // * 退款手续费最大值
    // */
    //@ApiModelProperty(value = "退款手续费最大值")
    //@Column(name ="refund_max_tate")
    //private BigDecimal refundMaxTate;
    ///**
    // * 退款费率
    // */
    //@ApiModelProperty(value = "退款费率")
    //@Column(name ="refund_rate")
    //private BigDecimal refundRate;
    ///**
    // * 退款附加值
    // */
    //@ApiModelProperty(value = "退款附加值")
    //@Column(name ="refund_add_value")
    //private BigDecimal refundAddValue;
    ///**
    // * 结算周期 (T+0,T+3,...)
    // */
    //@ApiModelProperty(value = "结算周期 (T+0,T+3,...)")
    //@Column(name ="settle_cycle")
    //private String settleCycle;
    ///**
    // * 审核状态 1-待审核 2-审核通过 3-审核不通过
    // */
    //@ApiModelProperty(value = "审核状态 1-待审核 2-审核通过 3-审核不通过")
    //@Column(name ="audit_status")
    //private Byte auditStatus;
    @ApiModelProperty(value = "状态")
    private Boolean enabled;
    @ApiModelProperty(value = "生效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String effectTime;
    /**
     * 审核备注
     */
    @ApiModelProperty(value = "审核备注")
    private String auditRemark;




}
