package com.asianwallets.common.response;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
@ApiModel(value = "登录响应实体", description = "登录响应实体")
public class AuthenticationResponse {

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("系统")
    private String sysId;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("用户账户")
    private String username;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("公钥")
    private String publicKey;

    @ApiModelProperty("机构logo")
    private String institutionLogo;

    @ApiModelProperty(value = "是否开通DCC")
    private Boolean dcc;

    @ApiModelProperty("pos机需要的加密的md5key")
    private String md5Key;

    @ApiModelProperty("角色集合")
    private List<ResRole> role;

    @ApiModelProperty("权限集合")
    private Set<ResPermissions> permissions;

}

