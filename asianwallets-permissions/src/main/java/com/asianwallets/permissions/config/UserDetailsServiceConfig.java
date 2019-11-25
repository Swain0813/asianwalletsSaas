package com.asianwallets.permissions.config;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.SysMenuVO;
import com.asianwallets.common.vo.SysRoleVO;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.service.SysUserService;
import com.asianwallets.permissions.utils.SpringSecurityUser;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

/**
 * SpringSecurity获取用户信息配置类
 **/
@Configuration
@Slf4j
public class UserDetailsServiceConfig implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        SysUserVO user = sysUserService.getSysUser(userName, null);
        if (user == null) {
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<SysRoleVO> roleList = user.getRole();
        Set<String> menuSet = Sets.newHashSet();
        //将该用户所有权限添加到集合
        for (SysRoleVO sysRoleVO : roleList) {
            if (sysRoleVO != null) {
                for (SysMenuVO sysMenuVO : sysRoleVO.getMenus()) {
                    menuSet.add(sysMenuVO.getId());
                }
            }
        }
        for (String menu : menuSet) {
            authorities.add(new SimpleGrantedAuthority(menu));
        }
        SpringSecurityUser userDetails = new SpringSecurityUser();
        userDetails.setSysUser(user);
        userDetails.setAuthorities(authorities);
        return userDetails;
    }
}
