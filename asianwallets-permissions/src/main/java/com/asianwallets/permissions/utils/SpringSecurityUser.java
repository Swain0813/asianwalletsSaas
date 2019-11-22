package com.asianwallets.permissions.utils;

import com.asianwallets.common.vo.SysUserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Delegate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@ApiModel(value = "用户VO", description = "用户VO")
public class SpringSecurityUser implements UserDetails {

    @Delegate
    @ApiModelProperty("用户信息输出VO")
    private SysUserVO sysUser;

    @ApiModelProperty("权限集合")
    private Collection<? extends GrantedAuthority> authorities;

    @ApiModelProperty("凭证是否不过期")
    private boolean credentialsNonExpired = true;
}
