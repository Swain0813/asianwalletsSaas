package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * OTA平台输入参数
 */
@Data
@ApiModel(value = "OTA平台输入参数", description = "OTA平台输入参数")
public class OtaChannelDTO extends BasePageHelper {

    @ApiModelProperty(value = "OTA平台id")
    private String id;

    @ApiModelProperty(value = "OTA平台名称")
    private String systemName;

    @ApiModelProperty(value = "是否支持撤销")
    private Boolean cancelDefault;

    @ApiModelProperty(value = "撤销url")
    private String cancelUrl;

    @ApiModelProperty(value = "上报url")
    private String reportUrl;

    @ApiModelProperty(value = "是否可以核销")
    private Boolean verificationDefault;

    @ApiModelProperty(value = "核销url")
    private String verificationUrl;

    @ApiModelProperty(value = "平台logo")
    private String systemImg;

    @ApiModelProperty(value = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @ApiModelProperty(value = "修改人")
    private String modifier;
}
