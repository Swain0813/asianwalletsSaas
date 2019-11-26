package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/3/5 17:04
 * @Description: 设备厂商输入实体
 */
@Data
@ApiModel(value = "设备厂商输入实体", description = "设备厂商输入实体")
public class DeviceVendorDTO extends BasePageHelper {

    @ApiModelProperty("厂商id")
    private String id;

    @ApiModelProperty("厂商中文名称")
    private String vendorCnName;

    @ApiModelProperty("厂商英文名称")
    private String vendorEnName;

    @ApiModelProperty("业务联系人")
    private String businessContact;

    @ApiModelProperty("联系方式")
    private String contactInformation;

    @ApiModelProperty("启用禁用")
    private Boolean enabled;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更新者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;


}
