package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author shenxinran
 * @Date: 2019/3/8 14:28
 * @Description: 设备绑定输入实体类
 */
@Data
@ApiModel(value = "设备绑定输入实体类", description = "设备绑定输入实体类")
public class DeviceBindingDTO extends BasePageHelper {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty(value = "设备信息id")
    private List<String> infoId;

    @ApiModelProperty(value = "机构Code")
    private String institutionCode;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "设备厂商名称")
    private String vendorName;

    @ApiModelProperty(value = "设备型号名称")
    private String modelName;

    @ApiModelProperty(value = "设备名称")
    private String infoName;

    @ApiModelProperty(value = "IMEI")
    private String imei;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "SN")
    private String sn;

    @ApiModelProperty(value = "操作员")
    private String operator;

    @ApiModelProperty(value = "使用类型")
    private String useType;//dic_8 1-购买  2-出租 3-商户自有 4-赠送 需要多语言

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "绑定时间")
    private Date bindingTime;

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
