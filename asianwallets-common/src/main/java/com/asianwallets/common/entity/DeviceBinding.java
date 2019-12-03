package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * 设备绑定
 */
@Data
@Table(name = "device_binding")
@ApiModel(value = "设备绑定", description = "设备绑定")
public class DeviceBinding extends BaseEntity {

    @ApiModelProperty(value = "设备信息id")
    @Column(name = "info_id")
    private String infoId;

    @ApiModelProperty(value = "机构id")
    @Column(name = "institution_code")
    private String institutionCode;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "商户id")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "代理商id")
    @Column(name = "agent_id")
    private String agentId;

    @ApiModelProperty(value = "商户名称")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "设备厂商名称")
    @Column(name = "vendor_name")
    private String vendorName;

    @ApiModelProperty(value = "设备型号名称")
    @Column(name = "model_name")
    private String modelName;

    @ApiModelProperty(value = "设备名称")
    @Column(name = "info_name")
    private String infoName;

    @ApiModelProperty(value = "IMEI")
    @Column(name = "imei")
    private String imei;

    @ApiModelProperty(value = "SN")
    @Column(name = "sn")
    private String sn;

    @ApiModelProperty(value = "操作员")
    @Column(name = "operator")
    private String operator;

    @ApiModelProperty(value = "使用用途")
    @Column(name = "use_type")
    private String useType;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "绑定时间")
    @Column(name = "binding_time")
    private Date bindingTime;


}