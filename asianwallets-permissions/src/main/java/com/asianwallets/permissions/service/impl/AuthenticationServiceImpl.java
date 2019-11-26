package com.asianwallets.permissions.service.impl;

import com.alibaba.fastjson.JSON;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 认证业务接口实现类
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private RedisService redisService;

    @Value("${security.jwt.token_expire_hour}")
    private int time;

    /**
     * 登录
     *
     * @param request 登陆输入实体
     * @return 登录响应实体
     */
    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        log.info("===========【登录接口】==========【请求参数】 request: {}", JSON.toJSONString(request));
        String username = request.getUsername();
        SysUserVO sysUserVO = sysUserService.getSysUser(username);
        if (sysUserVO == null) {
            log.info("===========【登录接口】==========【用户不存在!】");
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        //调用SpringSecurity底层AuthenticationManager(实际工作的类-DaoAuthenticationProvider)校验用户名与密码
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        //将Authentication对象放入SpringSecurity安全上下文环境中
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //封装登录响应实体
        AuthenticationResponse response = getAuthenticationResponse(sysUserVO);
        if (StringUtils.isNotBlank(response.getToken())) {
            //将用户信息存入Redis
            redisService.set(response.getToken(), JSON.toJSONString(sysUserVO), time * 60 * 60);
        }
        return response;
    }

    /**
     * 封装登录响应实体
     *
     * @param sysUserVO 用户信息输出实体
     * @return 登录响应实体
     */
    private AuthenticationResponse getAuthenticationResponse(SysUserVO sysUserVO) {
        AuthenticationResponse response = new AuthenticationResponse();
        //生成Token
        String token = tokenUtils.generateToken(sysUserVO.getUsername());
        List<ResRole> roles = Lists.newArrayList();
        Set<ResPermissions> permissions = Sets.newHashSet();
        for (SysRoleVO sysRoleVO : sysUserVO.getRole()) {
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
        response.setUserId(sysUserVO.getId());
        response.setInstitutionId(sysUserVO.getInstitutionId());
        response.setUsername(sysUserVO.getUsername());
        response.setName(sysUserVO.getName());
        response.setRole(roles);
        response.setPermissions(permissions);
        response.setToken(token);
        return response;
    }
}
