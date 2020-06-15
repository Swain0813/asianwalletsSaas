package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "机构权益查询VO", description = "机构权益查询VO")
public class InstitutionRightsVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "权益类型：1-满减 2-折扣 3-套餐 4-定额")
    private Byte rightsType;

    @ApiModelProperty(value = "活动主题")
    private String activityTheme;

    @ApiModelProperty(value = "活动数量")
    private Integer activityAmount;

    @ApiModelProperty(value = "剩余数量")
    private Integer surplusAmount;

    @ApiModelProperty(value = "活动开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "活动周期")
    private String activityTime;

    @ApiModelProperty(value = "分销价")
    private BigDecimal distributionPrice;

    @ApiModelProperty(value = "是否叠加")
    private Boolean overlay;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date useTime;

    @ApiModelProperty(value = "套餐金额")
    private BigDecimal packageValue;

    @ApiModelProperty(value = "票面金额")
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
    private String setImages;

    @ApiModelProperty(value = "规则说明")
    private String ruleDescription;

    @ApiModelProperty(value = "权益币种")
    private String rightsCurrency;

    @ApiModelProperty(value = "用作不可用时间")
    private String extend1;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}