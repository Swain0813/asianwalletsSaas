package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.dto.SysRoleMenuDto;
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
    public BaseResponse addSysUser(@RequestBody @ApiParam SysUserRoleDto sysUserRoleDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysUserRoleDto),
                "添加用户信息"));
        return ResultUtil.success(sysUserService.addSysUser(getSysUserVO().getUsername(), sysUserRoleDto));
    }

    @ApiOperation(value = "修改用户角色,用户权限信息")
    @PostMapping("/updateSysUser")
    public BaseResponse updateSysUser(@RequestBody @ApiParam SysUserRoleDto sysUserRoleDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysUserRoleDto),
                "修改用户信息"));
        return ResultUtil.success(sysUserService.updateSysUser(getSysUserVO().getUsername(), sysUserRoleDto));
    }

    @ApiOperation(value = "添加角色权限信息")
    @PostMapping("/addSysRole")
    public BaseResponse addSysRole(@RequestBody @ApiParam SysRoleMenuDto sysRoleMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysRoleMenuDto),
                "添加角色权限信息"));
        return ResultUtil.success(sysUserService.addSysRole(getSysUserVO().getUsername(), sysRoleMenuDto));
    }
}
