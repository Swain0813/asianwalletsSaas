package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "机构权益输入DTO", description = "机构权益输入DTO")
public class InstitutionRightsDTO extends BasePageHelper {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private List<String> merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    //----------------相同-------------------

    @NotNull(message = "50002")
    @ApiModelProperty(value = "优惠类型：1-满减 2-折扣 3-套餐 4-定额")
    private Byte rightsType;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "活动主题")
    private String activityTheme;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "活动数量")
    private Integer activityAmount;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "活动起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "不可使用时间")
    private List<String> unusableTime;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "使用限制 1，不限；2、每人/张/天 3、仅限1张 /人")
    private String getLimit;

    @ApiModelProperty(value = "可用门店地址")
    private String shopAddresses;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "规则说明")
    private String ruleDescription;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "票券币种")
    private String rightsCurrency;

    //------------------不同----------------------

    @ApiModelProperty(value = "套餐(票券)金额")
    private BigDecimal packageValue;

    @ApiModelProperty(value = "票券金额")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "满减金额")
    private BigDecimal fullReductionAmount;

    @ApiModelProperty(value = "扣率")
    private BigDecimal discount;

    @ApiModelProperty(value = "封顶金额")
    private BigDecimal capAmount;

    @ApiModelProperty(value = "是否叠加  0-否 1-是")
    private Boolean overlay;

    @ApiModelProperty(value = "套餐文字")
    private String setText;

    @ApiModelProperty(value = "套餐图片")
    private String setImages;

    @ApiModelProperty(value = "分销价")
    private BigDecimal distributionPrice;

    @ApiModelProperty(value = "抵扣金额")//满减填
    private BigDecimal deductionAmount;

    //-----------------------------------------
    @ApiModelProperty(value = "当前请求时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nowTime;

    @ApiModelProperty(value = "创建开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createStartTime;

    @ApiModelProperty(value = "创建结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createEndTime;

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


}