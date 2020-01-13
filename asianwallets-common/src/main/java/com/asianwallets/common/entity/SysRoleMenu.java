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
@Table(name = "sys_role_menu")
@ApiModel(value = "角色权限中间表", description = "角色权限中间表")
public class SysRoleMenu extends BaseEntity {

    @ApiModelProperty(value = "角色ID")
    @Column(name = "role_id")
    private String roleId;

    @ApiModelProperty(value = "权限ID")
    @Column(name = "menu_id")
    private String menuId;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled = true;
}