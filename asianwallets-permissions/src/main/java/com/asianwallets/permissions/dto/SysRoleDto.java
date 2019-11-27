package com.asianwallets.permissions.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "角色输入实体", description = "角色输入实体")
public class SysRoleDto extends BasePageHelper {

    @ApiModelProperty(value = "角色ID")
    private String roleId;

    @ApiModelProperty(value = "角色名字")
    private String roleName;

    @ApiModelProperty(value = "系统ID")
    private String sysId;

    @ApiModelProperty(value = "权限类型(1-运营,2-机构,3-商户,4-代理商,5-pos机)")
    private Integer permissionType;

    @ApiModelProperty(value = "角色名字")
    private Boolean enabled;
}
