package com.asianwallets.permissions.service;

import com.asianwallets.common.response.AuthenticationRequest;
import com.asianwallets.common.response.AuthenticationResponse;

/**
 * 认证业务接口
 */
public interface AuthenticationService {

    /**
     * 登陆
     *
     * @param request 登陆输入实体
     */
    AuthenticationResponse login(AuthenticationRequest request);

}
