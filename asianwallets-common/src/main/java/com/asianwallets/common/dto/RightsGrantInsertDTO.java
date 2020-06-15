package com.asianwallets.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "权益发放管理新增输入参数", description = "权益发放管理新增输入参数")
public class RightsGrantInsertDTO {

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "机构权益订单号")
    private String batchNo;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "发放平台")
    private String systemName;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "上报权益url")
    private String reportUrl;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "权益类型")//1-满减  2-折扣 3-套餐
    private Byte rightsType;

    @ApiModelProperty(value = "票券状态")//1-待领取 2-未使用 3-已使用 4-已过期 5-已退款
    private Byte ticketStatus;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "二级商户编号")
    private String merchantId;

    @ApiModelProperty(value = "二级商户名称")
    private String merchantName;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "活动主题")
    private String activityTheme;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "活动数量")
    private Integer activityAmount;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "活动开始时间")
    private String startTime;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "活动结束时间")
    private String endTime;

    @ApiModelProperty(value = "使用时间")
    private String useTime;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "票券金额")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "票券购买价")
    private BigDecimal ticketBuyPrice;

    @ApiModelProperty(value = "团购号")
    private String dealId;

    @ApiModelProperty(value = "发放平台返回的交易流水号")
    private String systemOrderId;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户手机号")
    private String mobileNo;

    @ApiModelProperty(value = "票券编号")
    private String ticketId;

    @ApiModelProperty(value = "套餐金额")
    private BigDecimal packageValue;

    @ApiModelProperty(value = "满减金额")
    private BigDecimal fullReductionAmount;

    @ApiModelProperty(value = "扣率")
    private BigDecimal discount;

    @ApiModelProperty(value = "封顶金额")
    private BigDecimal capAmount;

    @ApiModelProperty(value = "分销价")
    private BigDecimal distributionPrice;

    @ApiModelProperty(value = "使用限制")//1-不限 2-每订单/张
    private Byte getLimit;

    @ApiModelProperty(value = "可用门店地址")//(可以是多个用逗号分隔)
    private String shopAddresses;

    @ApiModelProperty(value = "套餐文字")
    private String setText;

    @ApiModelProperty(value = "套餐图片")
    private String setImages;

    @ApiModelProperty(value = "规则说明")
    private String ruleDescription;

    @ApiModelProperty(value = "权益币种")
    private String rightsCurrency;

    @ApiModelProperty(value = "服务器回调地址")
    private String serverUrl;

    @ApiModelProperty(value = "领取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date getTime;

    @ApiModelProperty(value = "核销时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelVerificationTime;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "备注1")
    private String ext1;

    @ApiModelProperty(value = "备注2")
    private String ext2;

    @ApiModelProperty(value = "备注3")
    private String ext3;

    @ApiModelProperty(value = "不可使用时间")
    private String ext4;

    @ApiModelProperty(value = "备注5")
    private String ext5;

    @ApiModelProperty(value = "备注6")
    private String ext6;

    @ApiModelProperty(value = "备注7")
    private String ext7;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;
}
