package com.asianwallets.common.entity;
import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "institution_rights")
@ApiModel(value = "机构权益", description = "机构权益")
public class InstitutionRights extends BaseEntity {

    @ApiModelProperty(value = "批次号")
    @Column(name = "batch_no")
    private String batchNo;

    @ApiModelProperty(value = "机构请求时间")
    @Column(name = "institution_request_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date institutionRequestTime;

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "经营类目")
    @Column(name = "business_category")
    private String businessCategory;

    @ApiModelProperty(value = "店铺编号")
    @Column(name = "shop_id")
    private String shopId;

    @ApiModelProperty(value = "店铺名")
    @Column(name = "shop_name")
    private String shopName;

    @ApiModelProperty(value = "店铺地址")
    @Column(name = "shop_address")
    private String shopAddress;

    @ApiModelProperty(value = "店铺电话")
    @Column(name = "shop_phone")
    private String shopPhone;

    @ApiModelProperty(value = "店铺邮箱")
    @Column(name = "shop_email")
    private String shopEmail;

    @ApiModelProperty(value = "权益类型：1-满减 2-折扣 3-套餐 4-定额")
    @Column(name = "rights_type")
    private Byte rightsType;

    @ApiModelProperty(value = "活动主题")
    @Column(name = "activity_theme")
    private String activityTheme;

    @ApiModelProperty(value = "活动数量")
    @Column(name = "activity_amount")
    private Integer activityAmount;

    @ApiModelProperty(value = "剩余数量")
    @Column(name = "surplus_amount")
    private Integer surplusAmount;

    @ApiModelProperty(value = "活动开始时间")
    @Column(name = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "不可使用开始时间")
    @Column(name = "unusable_start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date unusableStartTime;

    @ApiModelProperty(value = "不可使用结束时间")
    @Column(name = "unusable_end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date unusableEndTime;

    @ApiModelProperty(value = "使用时间")
    @Column(name = "use_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date useTime;

    @ApiModelProperty(value = "套餐金额")
    @Column(name = "package_value")
    private BigDecimal packageValue;

    @ApiModelProperty(value = "票面金额")
    @Column(name = "ticket_amount")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "抵扣金额")
    @Column(name = "deduction_amount")
    private BigDecimal deductionAmount;

    @ApiModelProperty(value = "满减金额")
    @Column(name = "full_reduction_amount")
    private BigDecimal fullReductionAmount;

    @ApiModelProperty(value = "扣率")
    @Column(name = "discount")
    private BigDecimal discount;

    @ApiModelProperty(value = "封顶金额")
    @Column(name = "cap_amount")
    private BigDecimal capAmount;

    @ApiModelProperty(value = "分销价")
    @Column(name = "distribution_price")
    private BigDecimal distributionPrice;

    @ApiModelProperty(value = "是否叠加")
    @Column(name = "overlay")
    private Boolean overlay;

    @ApiModelProperty(value = "使用限制 1，不限；2、每人/张/天 3、仅限1张 /人")
    @Column(name = "get_limit")
    private Byte getLimit;

    @ApiModelProperty(value = "可用门店地址")
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

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "用作不可用时间")
    @Column(name = "extend1")
    private String extend1;

    @ApiModelProperty(value = "备注2")
    @Column(name = "extend2")
    private String extend2;

    @ApiModelProperty(value = "备注3")
    @Column(name = "extend3")
    private String extend3;

    @ApiModelProperty(value = "备注4")
    @Column(name = "extend4")
    private String extend4;

    @ApiModelProperty(value = "备注5")
    @Column(name = "extend5")
    private String extend5;

    @ApiModelProperty(value = "备注6")
    @Column(name = "extend6")
    private String extend6;
}