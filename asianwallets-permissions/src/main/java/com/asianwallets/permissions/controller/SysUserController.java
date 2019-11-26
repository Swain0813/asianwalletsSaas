package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.dto.SysUserRoleDto;
import com.asianwallets.permissions.service.OperationLogService;
import com.asianwallets.permissions.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@Api(description = "用户接口")
@RequestMapping("/permission")
public class SysUserController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation(value = "添加用户角色,用户权限信息")
    @PostMapping("/addSysUser")
    public BaseResponse addSysUser(@RequestBody @ApiParam @Valid SysUserRoleDto sysUserRoleDto) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysUserRoleDto),
                "添加用户信息"));
        return ResultUtil.success(sysUserService.addSysUser(this.getSysUserVO().getUsername(), sysUserRoleDto));
    }
}
