package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 字典数据实体类
 *
 * @author shenxinran
 */
@Data
@Table(name = "dictionary")
@ApiModel(value = "字典数据类型", description = "字典数据")
public class Dictionary extends BaseEntity {

    @ApiModelProperty(value = "字典类型code")
    @Column(name = "dictype_code")
    private String dictypeCode;

    @ApiModelProperty(value = "类型code")
    @Column(name = "code")
    private String code;

    @ApiModelProperty(value = "名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "图标")
    @Column(name = "icon")
    private String icon;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "语种")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "币种默认值")
    @Column(name = "default_value")
    private String defaultValue;


}