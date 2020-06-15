package com.asianwallets.common.dto;
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
public class RightsApiDTO {

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "机构请求时间")
    private String requestTime;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private List<String> merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "权益类型：1-满减 2-折扣 3-套餐 4-定额")
    private Byte preferentialType;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "活动主题")
    private String activityTheme;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "活动数量")
    private Integer numberOfActivities;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "活动开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date activityStartTime;

    @NotNull(message = "50002")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "活动结束时间")
    private Date activityEndTime;

    @ApiModelProperty(value = "不可使用时间")
    private List<String> unusableTime;

    @ApiModelProperty(value = "是否叠加  0-否 1-是")
    private Boolean stackUsing;

    @ApiModelProperty(value = "抵扣金额")
    private BigDecimal deductionAmount;

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

    @NotNull(message = "50002")
    @ApiModelProperty(value = "使用限制 1，不限；2、每人/张/天 3、仅限1张 /人")
    private Byte useLimit;

    @ApiModelProperty(value = "可用门店")
    private String availableStores;

    @ApiModelProperty(value = "套餐文字")
    private String packageDetails;

    @ApiModelProperty(value = "套餐图片")
    private String packagePicture;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "规则说明")
    private String ruleDescription;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "权益币种")
    private String currency;

    @ApiModelProperty(value = "备注")
    private String remark;

    @NotNull(message = "52008")
    @ApiModelProperty(value = "签名")
    private String sign;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "签名方式")//1为RSA 2为MD5
    private String signType;
}