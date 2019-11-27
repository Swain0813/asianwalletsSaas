package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 字典类型实体类
 *
 * @author shenxinran
 */
@Data
@Table(name = "dictionary_type")
@ApiModel(value = "字典类型", description = "字典类型")
public class DictionaryType extends BaseEntity {

    @ApiModelProperty(value = "类型code")
    @Column(name = "dic_code")
    private String dicCode;

    @ApiModelProperty(value = "类型name")
    @Column(name = "dic_value")
    private String dicValue;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;


}