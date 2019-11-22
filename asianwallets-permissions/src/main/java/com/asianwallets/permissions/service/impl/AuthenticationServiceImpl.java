package com.asianwallets.permissions.service.impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.*;
import com.asianwallets.common.vo.SysMenuVO;
import com.asianwallets.common.vo.SysRoleVO;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.service.AuthenticationService;
import com.asianwallets.permissions.service.SysUserService;
import com.asianwallets.permissions.utils.TokenUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 权限认证业务接口实现类
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier(value = "userDetailsServiceConfig")
    private UserDetailsService userDetailsService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private RedisService redisService;

    @Value("${security.jwt.token_expire_hour}")
    private int time;

    /**
     * 登陆
     *
     * @param request 登陆输入实体
     */
    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        String username = request.getUsername();
        SysUserVO sysUserVO = sysUserService.getSysUser(username);
        if (sysUserVO == null) {
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                (username, request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return getAuthenticationResponse(username);
    }

    private AuthenticationResponse getAuthenticationResponse(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        SysUserVO sysUser = sysUserService.getSysUser(username);
        String token = tokenUtils.generateToken(userDetails);
        AuthenticationResponse response = new AuthenticationResponse(token);
        response.setUserId(sysUser.getId());
        response.setInstitutionId(sysUser.getInstitutionId());
        response.setUsername(sysUser.getUsername());
        response.setName(sysUser.getName());
        List<ResRole> roles = Lists.newArrayList();
        Set<ResPermissions> permissions = Sets.newHashSet();
        for (SysRoleVO sysRoleVO : sysUser.getRole()) {
            ResRole resRole = new ResRole();
            if (StringUtils.isNotBlank(sysRoleVO.getRoleName())) {
                BeanUtils.copyProperties(sysRoleVO, resRole);
                roles.add(resRole);
            }
            for (SysMenuVO sysMenuVO : sysRoleVO.getMenus()) {
                ResPermissions resPermissions = new ResPermissions();
                BeanUtils.copyProperties(sysMenuVO, resPermissions);
                permissions.add(resPermissions);
            }
        }
        response.setRole(roles);
        response.setPermissions(permissions);
        return response;
    }
}
