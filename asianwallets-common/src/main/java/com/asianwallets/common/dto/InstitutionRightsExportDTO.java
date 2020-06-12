package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "机构权益导出输入DTO", description = "机构权益导出输入DTO")
public class InstitutionRightsExportDTO {


    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;


    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "店铺编号")
    private String shopId;

    @ApiModelProperty(value = "店铺名")
    private String shopName;

    @ApiModelProperty(value = "店铺地址")
    private String shopAddress;

    @ApiModelProperty(value = "店铺电话")
    private String shopPhone;

    @ApiModelProperty(value = "店铺邮箱")
    private String shopEmail;

    @ApiModelProperty(value = "创建开始时间")
    private Date createStartTime;

    @ApiModelProperty(value = "创建结束时间")
    private Date createEndTime;

}