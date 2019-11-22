package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "sys_role")
@ApiModel(value = "角色表", description = "角色表")
public class SysRole extends BaseEntity {

    @ApiModelProperty(value = "机构ID")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "角色类型(1-机构,2-商户,3-pos机,4-代理商)")
    @Column(name = "type")
    private Integer type;

    @ApiModelProperty(value = "角色名称")
    @Column(name = "role_name")
    private String roleName;

    @ApiModelProperty(value = "角色编号")
    @Column(name = "role_code")
    private String roleCode;

    @ApiModelProperty(value = "描述")
    @Column(name = "description")
    private String description;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private boolean enabled = true;

    @ApiModelProperty(value = "权重")
    @Column(name = "sort")
    private Integer sort = 0;

}