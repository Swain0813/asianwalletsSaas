package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 设备信息
 */
@Data
@Table(name = "device_info")
@ApiModel(value = "设备信息", description = "设备信息")
public class DeviceInfo extends BaseEntity {

    @ApiModelProperty(value = "厂商ID")
    @Column(name = "vendor_id")
    private String vendorId;

    @ApiModelProperty(value = "机构CODE")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "型号ID")
    @Column(name = "model_id")
    private String modelId;

    @ApiModelProperty(value = "设备名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "IMEI")
    @Column(name = "imei")
    private String imei;

    @ApiModelProperty(value = "SN")
    @Column(name = "sn")
    private String sn;

    @ApiModelProperty(value = "MAC")
    @Column(name = "mac")
    private String mac;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "绑定状态")
    @Column(name = "binding_status")
    private Boolean bindingStatus;

}