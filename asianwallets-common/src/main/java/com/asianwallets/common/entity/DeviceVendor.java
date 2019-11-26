package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 设备厂商
 */
@Data
@Table(name = "device_vendor")
@ApiModel(value = "设备厂商", description = "设备厂商")
public class DeviceVendor extends BaseEntity {
    @ApiModelProperty(value = "厂商中文名称")
    @Column(name = "vendor_cn_name")
    private String vendorCnName;

    @ApiModelProperty(value = "厂商英文名称")
    @Column(name = "vendor_en_name")
    private String vendorEnName;

    @ApiModelProperty(value = "业务联系人")
    @Column(name = "business_contact")
    private String businessContact;

    @ApiModelProperty(value = "联系方式")
    @Column(name = "contact_information")
    private String contactInformation;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "备注1")
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