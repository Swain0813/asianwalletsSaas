package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "商户报备DTO", description = "商户报备DTO")
public class MerchantReportDTO extends BasePageHelper {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名")
    private String merchantName;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "通道MCC编码")
    private String channelMcc;

    @ApiModelProperty(value = "通道子商户编号")
    private String subMerchantCode;

    @ApiModelProperty(value = "通道子商户名称")
    private String subMerchantName;

    @ApiModelProperty(value = "通道店铺名")
    private String shopName;

    @ApiModelProperty(value = "通道店铺编号")
    private String shopCode;

    @ApiModelProperty(value = "SubAPPID")
    private String subAppid;

    @ApiModelProperty(value = "网站类型")//APP WEB
    private String siteType;

    @ApiModelProperty(value = "网站url")
    private String siteUrl;

    @ApiModelProperty(value = "报备状态")
    private Boolean enabled;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "起始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "终端号")
    private String extend1;

    @ApiModelProperty(value = "备注2")
    private String extend2;
}