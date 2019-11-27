package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/3/6 15:11
 * @Description: 设备信息输出实体
 */
@Data
@ApiModel(value = "设备信息输出实体", description = "设备信息输出实体")
public class DeviceInfoVO {

    @ApiModelProperty("设备信息id")
    private String id;

    @ApiModelProperty(value = "厂商ID")
    private String vendorId;

    @ApiModelProperty(value = "厂商名称")
    private String vendorName;

    @ApiModelProperty(value = "机构ID")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "型号ID")
    private String modelId;

    @ApiModelProperty(value = "型号名称")
    private String modelName;

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "IMEI")
    private String imei;

    @ApiModelProperty(value = "SN")
    private String sn;

    @ApiModelProperty(value = "MAC")
    private String mac;

    @ApiModelProperty("启用禁用")
    private Boolean enabled;

    @ApiModelProperty("绑定状态")
    private Boolean bindingStatus;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更新者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;
}
