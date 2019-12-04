package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 语种实体
 */
@Data
@Table(name = "language")
@ApiModel(value = "语种", description = "语种实体")
public class Language extends BaseEntity {

    @ApiModelProperty(value = "语种的识别code")
    @Column(name = "lang_code")
    private String langCode;

    @ApiModelProperty(value = "语种的名称")
    @Column(name = "lang_name")
    private String langName;

    @ApiModelProperty(value = "语种的图标")
    @Column(name = "lang_icon")
    private String langIcon;


    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;


}