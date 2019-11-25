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
@Table(name = "sys_user")
@ApiModel(value = "用户", description = "用户")
public class SysUser extends BaseEntity {

    @ApiModelProperty(value = "用户账户")
    @Column(name = "username")
    private String username;

    @ApiModelProperty(value = "系统id")
    @Column(name = "sys_id")
    private String sysId;

    @ApiModelProperty(value = "系统类型(1-机构,2-商户)")
    @Column(name = "sys_type")
    private Integer sysType;

    @ApiModelProperty(value = "权限类型(1-运营,2-机构,3-商户,4-POS机)")
    @Column(name = "permission_type")
    private Integer permissionType;

    @ApiModelProperty(value = "密码")
    @Column(name = "password")
    private String password;

    @ApiModelProperty(value = "交易密码")
    @Column(name = "trade_password")
    private String tradePassword;

    @ApiModelProperty(value = "用户名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "手机")
    @Column(name = "mobile")
    private String mobile;

    @ApiModelProperty(value = "邮箱")
    @Column(name = "email")
    private String email;

    @ApiModelProperty(value = "语言")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled = true;

}
