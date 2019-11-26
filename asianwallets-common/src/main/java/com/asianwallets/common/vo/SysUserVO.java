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
    public String id;

    @ApiModelProperty("用户ID")
    public String institutionId;

    @ApiModelProperty("用户ID")
    public String username;

    @ApiModelProperty("用户ID")
    public Integer type;//1-运维 2-机构  3-pos机 4-代理商

    @ApiModelProperty("用户ID")
    public String password;

    @ApiModelProperty("用户ID")
    public String tradePassword;

    @ApiModelProperty("用户ID")
    public String name;

    @ApiModelProperty("用户ID")
    public String mobile;

    public String email;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date updateTime;

    @ApiModelProperty(value = "创建者")
    public String creator;

    @ApiModelProperty(value = "更改者")
    public String modifier;

    @ApiModelProperty(value = "备注")
    public String remark;

    public boolean accountNonExpired = true;

    public boolean accountNonLocked = true;

    public boolean enabled;

    public Date lastPasswordReset;

    @ApiModelProperty("角色集合")
    private List<SysRoleVO> role;


}
