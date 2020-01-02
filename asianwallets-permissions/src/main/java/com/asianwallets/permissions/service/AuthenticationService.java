package com.asianwallets.permissions.service;

import com.asianwallets.common.response.AuthenticationRequest;
import com.asianwallets.common.response.AuthenticationResponse;

/**
 * 认证业务接口
 */
public interface AuthenticationService {

    /**
     * 运营系统登录
     *
     * @param request 登陆输入实体
     */
    AuthenticationResponse operationLogin(AuthenticationRequest request);

    /**
     * 机构系统登录
     *
     * @param request 登陆输入实体
     */
    AuthenticationResponse institutionLogin(AuthenticationRequest request);

    /**
     * 商户系统登录
     *
     * @param request 登陆输入实体
     */
    AuthenticationResponse merchantLogin(AuthenticationRequest request);

    /**
     * 代理系统登录
     *
     * @param request 登陆输入实体
     */
    AuthenticationResponse agentLogin(AuthenticationRequest request);

    /**
     * Pos机系统登录
     *
     * @param request 登陆输入实体
     */
    AuthenticationResponse posLogin(AuthenticationRequest request);
}
