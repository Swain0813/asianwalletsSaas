package com.asianwallets.permissions.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.*;
import com.asianwallets.common.vo.SysMenuVO;
import com.asianwallets.common.vo.SysRoleVO;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.feign.base.InstitutionFeign;
import com.asianwallets.permissions.service.AuthenticationService;
import com.asianwallets.permissions.service.SysUserService;
import com.asianwallets.permissions.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private InstitutionFeign institutionFeign;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${security.jwt.token_expire_hour}")
    private int time;

    /**
     * 运营系统登录
     *
     * @param request 登陆输入实体
     * @return 登录响应实体
     */
    @Override
    public AuthenticationResponse operationLogin(AuthenticationRequest request) {
        log.info("===========【运营系统登录】==========【请求参数】 request: {}", JSON.toJSONString(request));
        if (StringUtils.isEmpty(request.getUsername())) {
            log.info("===========【运营系统登录】==========【用户名为空!】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (StringUtils.isEmpty(request.getPassword())) {
            log.info("===========【运营系统登录】==========【密码为空!】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        String username = request.getUsername();
        SysUserVO sysUserVO = sysUserService.getSysUser(username);
        if (sysUserVO == null) {
            log.info("===========【运营系统登录】==========【用户名不存在!】");
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
     * 机构系统登录
     *
     * @param request 登陆输入实体
     */
    @Override
    public AuthenticationResponse institutionLogin(AuthenticationRequest request) {
        log.info("===========【机构系统登录】==========【请求参数】 request: {}", JSON.toJSONString(request));
        if (StringUtils.isEmpty(request.getUsername())) {
            log.info("===========【机构系统登录】==========【用户名为空!】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (StringUtils.isEmpty(request.getPassword())) {
            log.info("===========【机构系统登录】==========【密码为空!】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (StringUtils.isEmpty(request.getSysId())) {
            log.info("===========【机构系统登录】==========【机构ID为空!】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        BaseResponse baseResponse = institutionFeign.getInstitutionInfoById(request.getSysId());
        Institution institution = objectMapper.convertValue(baseResponse.getData(), Institution.class);
        if (institution == null) {
            //机构信息不存在
            throw new BusinessException(EResultEnum.INSTITUTION_NOT_EXIST.getCode());
        }
        if (!institution.getEnabled()) {
            //机构已禁用
            throw new BusinessException(EResultEnum.INSTITUTION_IS_DISABLE.getCode());
        }
        String username = request.getUsername();
        SysUserVO sysUserVO = sysUserService.getSysUser(username);
        if (sysUserVO == null) {
            log.info("===========【机构系统登录】==========【用户名不存在!】");
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
     * 商户系统登录
     *
     * @param request 登陆输入实体
     */
    @Override
    public AuthenticationResponse merchantLogin(AuthenticationRequest request) {
        log.info("===========【商户系统登录】==========【请求参数】 request: {}", JSON.toJSONString(request));
        String username = request.getUsername();
        SysUserVO sysUserVO = sysUserService.getSysUser(username);
        if (sysUserVO == null) {
            log.info("===========【商户系统登录】==========【用户名不存在!】");
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
     * 代理系统登录
     *
     * @param request 登陆输入实体
     */
    @Override
    public AuthenticationResponse agentLogin(AuthenticationRequest request) {
        log.info("===========【代理系统登录】==========【请求参数】 request: {}", JSON.toJSONString(request));
        String username = request.getUsername();
        SysUserVO sysUserVO = sysUserService.getSysUser(username);
        if (sysUserVO == null) {
            log.info("===========【代理系统登录】==========【用户名不存在!】");
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
     * Pos机系统登录
     *
     * @param request 登陆输入实体
     */
    @Override
    public AuthenticationResponse posLogin(AuthenticationRequest request) {
        log.info("===========【Pos机系统登录】==========【请求参数】 request: {}", JSON.toJSONString(request));
        String username = request.getUsername();
        SysUserVO sysUserVO = sysUserService.getSysUser(username);
        if (sysUserVO == null) {
            log.info("===========【Pos机系统登录】==========【用户名不存在!】");
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
            redisService.set(response.getToken(), JSON.toJSONString(sysUserVO));
        }
        return response;
    }

    /**
     * 对外API线下交易登录
     *
     * @param request 登陆输入实体
     */
    @Override
    public AuthenticationResponse terminalLogin(AuthenticationRequest request) {
        log.info("===========【对外API线下交易登录】==========【请求参数】 request: {}", JSON.toJSONString(request));
        String username = request.getUsername();
        SysUserVO sysUserVO = sysUserService.getSysUser(username);
        if (sysUserVO == null) {
            log.info("===========【对外API线下交易登录】==========【用户名不存在!】");
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
            redisService.set(response.getToken(), JSON.toJSONString(sysUserVO));
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
        response.setSysId(sysUserVO.getSysId());
        response.setUsername(sysUserVO.getUsername());
        response.setName(sysUserVO.getName());
        response.setRole(roles);
        response.setPermissions(permissions);
        response.setToken(token);
        return response;
    }
}
