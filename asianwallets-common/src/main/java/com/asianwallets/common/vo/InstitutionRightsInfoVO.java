package com.asianwallets.common.vo;

import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "机构权益详情", description = "机构权益详情")
public class InstitutionRightsInfoVO extends BaseEntity {

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "机构请求时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date institutionRequestTime;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "二级商户编号")
    private List<String> merchantIds;

    @ApiModelProperty(value = "二级商户名称")
    private List<String> merchantNames;

    @ApiModelProperty(value = "经营类目")
    private String businessCategory;

    @ApiModelProperty(value = "店铺编号")
    private String shopId;

    @ApiModelProperty(value = "店铺名")
    private String shopName;

    @ApiModelProperty(value = "店铺地址")
    private String shopAddress;

    @ApiModelProperty(value = "店铺电话")
    private String shopPhone;

    @ApiModelProperty(value = "店铺邮箱")
    private String shopEmail;

    @ApiModelProperty(value = "权益类型：1-满减 2-折扣 3-套餐 4-定额")
    private Byte rightsType;

    @ApiModelProperty(value = "活动主题")
    private String activityTheme;

    @ApiModelProperty(value = "活动数量")
    private Integer activityAmount;

    @ApiModelProperty(value = "剩余数量")
    private Integer surplusAmount;

    @ApiModelProperty(value = "是否叠加  0-否 1-是")
    private Boolean overlay;

    @ApiModelProperty(value = "活动开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "不可用时间")
    List<String> unusableTime;

    @ApiModelProperty(value = "使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date useTime;

    @ApiModelProperty(value = "套餐金额")
    private BigDecimal packageValue;

    @ApiModelProperty(value = "票券金额")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "抵扣金额")
    private BigDecimal deductionAmount;

    @ApiModelProperty(value = "满减金额")
    private BigDecimal fullReductionAmount;

    @ApiModelProperty(value = "扣率")
    private BigDecimal discount;

    @ApiModelProperty(value = "封顶金额")
    private BigDecimal capAmount;

    @ApiModelProperty(value = "领取限制 1-不限 2-每订单/张")
    private Byte getLimit;

    @ApiModelProperty(value = "可用门店地址")
    private String shopAddresses;

    @ApiModelProperty(value = "套餐文字")
    private String setText;

    @ApiModelProperty(value = "套餐图片")
    private List setImages;

    @ApiModelProperty(value = "规则说明")
    private String ruleDescription;

    @ApiModelProperty(value = "权益币种")
    private String rightsCurrency;

    @ApiModelProperty(value = "服务器回调地址")
    private String serverUrl;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "备注1")
    private String extend1;

    @ApiModelProperty(value = "备注2")
    private String extend2;

    @ApiModelProperty(value = "备注3")
    private String extend3;

    @ApiModelProperty(value = "备注4")
    private String extend4;

    @ApiModelProperty(value = "备注5")
    private String extend5;

    @ApiModelProperty(value = "备注6")
    private String extend6;
}