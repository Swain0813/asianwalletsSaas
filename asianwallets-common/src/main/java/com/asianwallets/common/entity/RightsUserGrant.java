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
import java.util.Date;

@Data
@Entity
@Table(name = "rights_user_grant")
@ApiModel(value = "票券表", description = "票券表")
public class RightsUserGrant extends BaseEntity {

    @ApiModelProperty(value = "分销平台")
    @Column(name = "system_name")
    private String systemName;

    @ApiModelProperty(value = "权益发放批次号")
    @Column(name = "batch_no")
    private String batchNo;

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "二级商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "二级商户名称")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "权益类型")//-满减 2-折扣 3-套餐 4-定额
    @Column(name = "rights_type")
    private Byte rightsType;

    @ApiModelProperty(value = "活动主题")
    @Column(name = "activity_theme")
    private String activityTheme;

    @ApiModelProperty(value = "活动数量")
    @Column(name = "activity_amount")
    private Integer activityAmount;

    @ApiModelProperty(value = "活动开始时间")
    @Column(name = "start_time")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    @Column(name = "end_time")
    private Date endTime;

    @ApiModelProperty(value = "使用时间")
    @Column(name = "use_time")
    private Date useTime;

    @ApiModelProperty(value = "不可使用开始时间")
    @Column(name = "unusable_start_time")
    private Date unusableStartTime;

    @ApiModelProperty(value = "不可使用结束时间")
    @Column(name = "unusable_end_time")
    private Date unusableEndTime;

    @ApiModelProperty(value = "团购号")
    @Column(name = "deal_id")
    private String dealId;

    @ApiModelProperty(value = "发放平台返回的交易流水号")
    @Column(name = "system_order_id")
    private String systemOrderId;

    @ApiModelProperty(value = "用户id")
    @Column(name = "user_id")
    private String userId;

    @ApiModelProperty(value = "用户手机号")
    @Column(name = "mobile_no")
    private String mobileNo;

    @ApiModelProperty(value = "领取数量")
    @Column(name = "get_amount")
    private Integer getAmount;

    @ApiModelProperty(value = "核销数量")
    @Column(name = "cancel_verification_amount")
    private Integer cancelVerificationAmount;

    @ApiModelProperty(value = "剩余数量")
    @Column(name = "surplus_amount")
    private Integer surplusAmount;

    @ApiModelProperty(value = "票券编号")
    @Column(name = "ticket_id")
    private String ticketId;

    @ApiModelProperty(value = "票券状态")//1-待领取 2-未使用 3-已使用 4-已过期 5-已退款
    @Column(name = "ticket_status")
    private Byte ticketStatus;

    @ApiModelProperty(value = "票券金额")
    @Column(name = "ticket_amount")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "套餐金额")
    @Column(name = "package_value")
    private BigDecimal packageValue;

    @ApiModelProperty(value = "满减金额")
    @Column(name = "full_reduction_amount")
    private BigDecimal fullReductionAmount;

    @ApiModelProperty(value = "扣率")
    @Column(name = "discount")
    private BigDecimal discount;

    @ApiModelProperty(value = "封顶金额")
    @Column(name = "cap_amount")
    private BigDecimal capAmount;

    @ApiModelProperty(value = "抵扣金额")
    @Column(name = "deduction_amount")
    private BigDecimal deductionAmount;

    @ApiModelProperty(value = "票券购买价")
    @Column(name = "ticket_buy_price")
    private BigDecimal ticketBuyPrice;

    @ApiModelProperty(value = "分销价")
    @Column(name = "distribution_price")
    private BigDecimal distributionPrice;

    @ApiModelProperty(value = "是否叠加")
    @Column(name = "overlay")
    private Boolean overlay;

    @ApiModelProperty(value = "领取限制")//1-不限 2-每订单/张
    @Column(name = "get_limit")
    private Byte getLimit;

    @ApiModelProperty(value = "可用门店地址")//(可以是多个用逗号分隔)
    @Column(name = "shop_addresses")
    private String shopAddresses;

    @ApiModelProperty(value = "套餐文字")
    @Column(name = "set_text")
    private String setText;

    @ApiModelProperty(value = "套餐图片")
    @Column(name = "set_images")
    private String setImages;

    @ApiModelProperty(value = "规则说明")
    @Column(name = "rule_description")
    private String ruleDescription;

    @ApiModelProperty(value = "权益币种")
    @Column(name = "rights_currency")
    private String rightsCurrency;

    @ApiModelProperty(value = "服务器回调地址")
    @Column(name = "server_url")
    private String serverUrl;

    @ApiModelProperty(value = "领取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "get_time")
    private Date getTime;

    @ApiModelProperty(value = "核销时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "cancel_verification_time")
    private Date cancelVerificationTime;

    @ApiModelProperty(value = "ota活动开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "ota_start_time")
    private Date otaStartTime;

    @ApiModelProperty(value = "ota活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "ota_end_time")
    private Date otaEndTime;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "备注1")
    @Column(name = "ext1")
    private String ext1;

    @ApiModelProperty(value = "备注2")
    @Column(name = "ext2")
    private String ext2;

    @ApiModelProperty(value = "备注3")
    @Column(name = "ext3")
    private String ext3;

    @ApiModelProperty(value = "不可用时间")
    @Column(name = "ext4")
    private String ext4;

    @ApiModelProperty(value = "备注5")
    @Column(name = "ext5")
    private String ext5;

    @ApiModelProperty(value = "备注6")
    @Column(name = "ext6")
    private String ext6;

    @ApiModelProperty(value = "备注7")
    @Column(name = "ext7")
    private String ext7;
}
