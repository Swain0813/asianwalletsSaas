package com.asianwallets.permissions.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户输入实体", description = "用户输入实体")
public class SysUserDto extends BasePageHelper {

    @ApiModelProperty(value = "系统ID")
    private String sysId;

    @ApiModelProperty(value = "用户账户")
    private String username;

    @ApiModelProperty(value = "权限类型(1-运营,2-机构,3-商户,4-代理商,5-pos机)")
    private Integer permissionType;

    @ApiModelProperty(value = "角色id")
    private String roleId;

}
