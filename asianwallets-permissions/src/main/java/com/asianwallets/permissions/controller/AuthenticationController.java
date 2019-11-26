package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.response.*;
import com.asianwallets.permissions.service.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Api(description = "认证接口")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @ApiOperation(value = "运营系统登录")
    @PostMapping("/operationLogin")
    public BaseResponse operationLogin(@RequestBody @ApiParam @Valid AuthenticationRequest request) {
        return ResultUtil.success(authenticationService.operationLogin(request));
    }

    @ApiOperation(value = "机构系统登录")
    @PostMapping("/institutionLogin")
    public BaseResponse institutionLogin(@RequestBody @ApiParam @Valid AuthenticationRequest request) {
        return ResultUtil.success(authenticationService.institutionLogin(request));
    }

    @ApiOperation(value = "商户系统登录")
    @PostMapping("/merchantLogin")
    public BaseResponse merchantLogin(@RequestBody @ApiParam @Valid AuthenticationRequest request) {
        return ResultUtil.success(authenticationService.merchantLogin(request));
    }

    @ApiOperation(value = "代理商系统登录")
    @PostMapping("/agentLogin")
    public BaseResponse agentLogin(@RequestBody @ApiParam @Valid AuthenticationRequest request) {
        return ResultUtil.success(authenticationService.agentLogin(request));
    }

    @ApiOperation(value = "Pos机登录")
    @PostMapping("/posLogin")
    public BaseResponse posLogin(@RequestBody @ApiParam @Valid AuthenticationRequest request) {
        return ResultUtil.success(authenticationService.posLogin(request));
    }

    @ApiOperation(value = "对外API线下交易登录")
    @PostMapping("/terminalLogin")
    public BaseResponse terminalLogin(@RequestBody @ApiParam @Valid AuthenticationRequest request) {
        String token = authenticationService.terminalLogin(request);
        return ResultUtil.success(new JSONObject().put("token", token));
    }
}
