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

}