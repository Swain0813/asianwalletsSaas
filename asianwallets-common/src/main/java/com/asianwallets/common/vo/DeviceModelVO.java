package com.asianwallets.common.vo;

import com.asianwallets.common.base.BasePageHelper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/3/6 11:00
 * @Description: 设备型号输出实体
 */
@Data
@ApiModel(value = "设备型号输出实体", description = "设备型号输出实体")
public class DeviceModelVO extends BasePageHelper {

    @ApiModelProperty("设备型号id")
    private String id;

    @ApiModelProperty(value = "厂商ID")
    private String vendorId;

    @ApiModelProperty(value = "厂商名称")
    private String vendorName;

    @ApiModelProperty(value = "厂商英文名称")
    private String vendorEnName;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "设备类型名称")
    private String deviceTypeName;

    @ApiModelProperty(value = "设备型号")
    private String deviceName;

    @ApiModelProperty(value = "内存信息")
    private String ram;

    @ApiModelProperty(value = "操作系统")
    private String system;

    @ApiModelProperty(value = "网络类型")
    private String network;

    @ApiModelProperty(value = "网络类型名称")
    private String networkName;

    @ApiModelProperty(value = "打印机信息")
    private String printer;

    @ApiModelProperty(value = "分辨率信息")
    private String resolutionRatio;

    @ApiModelProperty(value = "入网认证编号")
    private String accessPermit;

    @ApiModelProperty(value = "读卡器信息")
    private String cardReader;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

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
