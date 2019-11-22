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
@Table(name = "sys_user_role")
@ApiModel(value = "用户角色中间表", description = "用户角色中间表")
public class SysUserRole extends BaseEntity {

    @ApiModelProperty(value = "用户ID")
    @Column(name = "user_id")
    private String userId;

    @ApiModelProperty(value = "角色ID")
    @Column(name = "role_id")
    private String roleId;

}