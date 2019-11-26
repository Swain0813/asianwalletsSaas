package com.asianwallets.permissions.controller;

import com.asianwallets.common.response.*;
import com.asianwallets.permissions.service.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Api(description = "权限认证接口")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @ApiOperation(value = "登陆")
    @PostMapping("/login")
    public BaseResponse login(@RequestBody AuthenticationRequest request) {
        return ResultUtil.success(authenticationService.login(request));
    }
}
