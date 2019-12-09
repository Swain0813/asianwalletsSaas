package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "支付方式", description = "支付方式")
public class PayTypeVO {


    @ApiModelProperty(value = "ID")
    public String id;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "支付方式名称")
    private String name;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "大图标")
    private String picon;

    @ApiModelProperty(value = "小图标")
    private String dicon;

    @ApiModelProperty(value = "付款类型 1 收 2 付")
    private Byte mode;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

}