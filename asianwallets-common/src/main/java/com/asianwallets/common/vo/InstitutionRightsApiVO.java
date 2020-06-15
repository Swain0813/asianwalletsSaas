package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
@ApiModel(value = "机构权益对外查询VO", description = "机构权益对外查询VO")
public class InstitutionRightsApiVO {

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "二级商户编号List")
    private List<String> merchantIds;

    @JsonIgnore
    @ApiModelProperty(value = "二级商户编号")
    private String merchantId;

    @ApiModelProperty(value = "权益类型：1-满减 2-折扣 3-套餐 4-定额")
    private Byte preferentialType;

    @ApiModelProperty(value = "活动主题")
    private String activityTheme;

    @ApiModelProperty(value = "活动数量")
    private Integer numberOfActivities;

    @ApiModelProperty(value = "是否叠加")
    private Boolean stackUsing;

    @ApiModelProperty(value = "活动开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date activityStartTime;

    @ApiModelProperty(value = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date activityEndTime;

    @ApiModelProperty(value = "不可使用时间")
    private List<String> unusableTime;

    @ApiModelProperty(value = "套餐金额")
    private BigDecimal packagePrice;

    @ApiModelProperty(value = "票券金额")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "满减金额")
    private BigDecimal consumptionAmount;

    @ApiModelProperty(value = "扣率")
    private BigDecimal discount;

    @ApiModelProperty(value = "封顶金额")
    private BigDecimal maximumDiscountAmount;

    @ApiModelProperty(value = "使用限制 1，不限；2、每人/张/天 3、仅限1张 /人")
    private Byte useLimit;

    @ApiModelProperty(value = "可用门店地址")
    private String shopAddresses;

    @ApiModelProperty(value = "套餐文字")
    private String packageDetails;

    @ApiModelProperty(value = "套餐图片")
    private String packagePicture;

    @ApiModelProperty(value = "规则说明")
    private String ruleDescription;

    @ApiModelProperty(value = "权益币种")
    private String currency;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonIgnore
    @ApiModelProperty(value = "extend1")
    private String extend1;
}