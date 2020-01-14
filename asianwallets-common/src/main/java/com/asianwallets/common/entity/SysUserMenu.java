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
@Table(name = "sys_user_menu")
@ApiModel(value = "用户权限中间表", description = "用户权限中间表")
public class SysUserMenu extends BaseEntity {

    @ApiModelProperty(value = "用户ID")
    @Column(name = "user_id")
    private String userId;

    @ApiModelProperty(value = "权限ID")
    @Column(name = "menu_id")
    private String menuId;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled = true;
}