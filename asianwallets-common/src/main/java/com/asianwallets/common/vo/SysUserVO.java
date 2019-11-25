package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-01-10 16:00
 **/
@Data
@ApiModel(value = "用户VO", description = "用户VO")
public class SysUserVO implements Serializable {

    @ApiModelProperty("用户ID")
    private String id;

    @ApiModelProperty("系统ID")
    private String SysId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("权限类型")
    private Integer permissionType;//1-运维 2-机构  3-pos机 4-代理商

    @ApiModelProperty("登录密码")
    private String password;

    @ApiModelProperty("交易密码")
    private String tradePassword;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean enabled;

    private Date lastPasswordReset;

    @ApiModelProperty("角色集合")
    private List<SysRoleVO> role;


}
