package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
@ApiModel(value = "节假日输入实体", description = "节假日输入实体")
public class HolidaysDTO extends BasePageHelper {

    @ApiModelProperty(value = "节假日id")
    private String id;

    @ApiModelProperty(value = "国家编号")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "节假日名称")
    private String name;

    @ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "起始日期")
    private String beginDate;

    @ApiModelProperty(value = "截止日期")
    private String endDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;
}