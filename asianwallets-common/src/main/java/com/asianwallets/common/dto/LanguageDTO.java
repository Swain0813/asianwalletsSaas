package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/1/29 14:17
 * @Description:
 */
@Data
@ApiModel(value = "LanguageDTO", description = "语种的输入实体")
public class LanguageDTO extends BasePageHelper {

    @ApiModelProperty(value = "语种的唯一ID")
    private String id;

    @ApiModelProperty(value = "语种的识别code")
    private String langCode;

    @ApiModelProperty(value = "语种的名称")
    private String langName;

    @ApiModelProperty(value = "语种的图标")
    private String langIcon;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

}
