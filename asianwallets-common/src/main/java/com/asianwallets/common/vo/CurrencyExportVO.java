package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 币种信息导出用
 */
@Data
@ApiModel(value = "币种信息输出实体", description = "币种信息输出实体")
public class CurrencyExportVO {

    @ApiModelProperty(value = "币种代码")
    private String code;

    @ApiModelProperty(value = "币种名称")
    private String name;

    @ApiModelProperty(value = "币种国家")
    private String remark;

    @ApiModelProperty(value = "币种最小单位")
    private String defaults;

    @ApiModelProperty(value = "币种状态")
    private String enabled;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
