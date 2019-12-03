package com.asianwallets.common.vo;

import com.asianwallets.common.base.BasePageHelper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/3/8 14:28
 * @Description: 设备绑定输出实体类
 */
@Data
@ApiModel(value = "设备绑定输出实体类", description = "设备绑定输出实体类")
public class DeviceBindingVO extends BasePageHelper {


    @ApiModelProperty(hidden = true)
    public String id;

    // 创建时间
    @ApiModelProperty(value = "创建时间")
    public Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "设备信息id")
    private String infoId;

    @ApiModelProperty(value = "机构id")
    private String institutionCode;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户id")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "代理商id")
    private String agentId;

    @ApiModelProperty(value = "代理商名称")
    private String agentName;

    @ApiModelProperty(value = "设备厂商名称")
    private String vendorName;

    @ApiModelProperty(value = "设备型号名称")
    private String modelName;

    @ApiModelProperty(value = "设备名称")
    private String infoName;

    @ApiModelProperty(value = "IMEI")
    private String imei;

    @ApiModelProperty(value = "SN")
    private String sn;

    @ApiModelProperty(value = "操作员")
    private String operator;

    @ApiModelProperty(value = "使用用途")
    private String useType;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "绑定时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date bindingTime;

    @ApiModelProperty(value = "使用类型名称")
    private String useTypeName;


}
