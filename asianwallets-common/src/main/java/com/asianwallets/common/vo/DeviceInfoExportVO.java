package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/3/6 15:11
 * @Description: 设备信息导出实体
 */
@Data
@ApiModel(value = "设备信息导出实体", description = "设备信息导出实体")
public class DeviceInfoExportVO {

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "设备厂家")
    private String vendorName;

    @ApiModelProperty(value = "设备型号")
    private String modelName;

    @ApiModelProperty(value = "IMEI")
    private String imei;

    @ApiModelProperty(value = "SN")
    private String sn;

    @ApiModelProperty("绑定状态")
    private Boolean bindingStatus;

    @ApiModelProperty(value = "操作员")
    private String creator;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty("状态")
    private Boolean enabled;

}
