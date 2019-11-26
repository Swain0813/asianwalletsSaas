package com.asianwallets.permissions.config;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.SysMenuVO;
import com.asianwallets.common.vo.SysRoleVO;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.dao.SysUserMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Configuration
@Slf4j
public class UserDetailServiceConfig implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserVO user = sysUserMapper.getSysUser(username);
        if (user == null) {
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<SysRoleVO> roleList = user.getRole();
        Set<String> set = Sets.newHashSet();
        for (SysRoleVO sysRoleVO : roleList) {
            if (sysRoleVO != null) {
                for (SysMenuVO permission : sysRoleVO.getMenus()) {
                    set.add(permission.getId());
                }
            }
        }
        for (String s : set) {
            authorities.add(new SimpleGrantedAuthority(s));
        }
        SpringSecurityUser userDetails = new SpringSecurityUser();
        userDetails.setSysUser(user);
        userDetails.setAuthorities(authorities);
        return userDetails;
    }
}
