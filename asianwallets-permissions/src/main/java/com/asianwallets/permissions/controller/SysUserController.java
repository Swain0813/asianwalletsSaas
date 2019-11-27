package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.dto.SysRoleMenuDto;
import com.asianwallets.permissions.dto.SysRoleSecDto;
import com.asianwallets.permissions.dto.SysUserRoleDto;
import com.asianwallets.permissions.dto.SysUserSecDto;
import com.asianwallets.permissions.service.OperationLogService;
import com.asianwallets.permissions.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@Api(description = "用户接口")
@RequestMapping("/permission")
public class SysUserController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation(value = "运营后台添加用户角色,用户权限信息")
    @PostMapping("/addSysUserByOperation")
    public BaseResponse addSysUserByOperation(@RequestBody @ApiParam SysUserRoleDto sysUserRoleDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysUserRoleDto),
                "运营后台添加用户信息"));
        return ResultUtil.success(sysUserService.addSysUserByOperation(getSysUserVO().getUsername(), sysUserRoleDto));
    }

    @ApiOperation(value = "运营后台修改用户角色,用户权限信息")
    @PostMapping("/updateSysUserByOperation")
    public BaseResponse updateSysUserByOperation(@RequestBody @ApiParam SysUserRoleDto sysUserRoleDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(sysUserRoleDto),
                "运营后台修改用户信息"));
        return ResultUtil.success(sysUserService.updateSysUserByOperation(getSysUserVO().getUsername(), sysUserRoleDto));
    }

    @ApiOperation(value = "添加角色权限信息")
    @PostMapping("/addSysRole")
    public BaseResponse addSysRole(@RequestBody @ApiParam SysRoleMenuDto sysRoleMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysRoleMenuDto),
                "添加角色权限信息"));
        return ResultUtil.success(sysUserService.addSysRole(getSysUserVO().getUsername(), sysRoleMenuDto));
    }

    @ApiOperation(value = "修改角色权限信息")
    @PostMapping("/updateSysRole")
    public BaseResponse updateSysRole(@RequestBody @ApiParam SysRoleMenuDto sysRoleMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(sysRoleMenuDto),
                "修改角色权限信息"));
        return ResultUtil.success(sysUserService.updateSysRole(getSysUserVO().getUsername(), sysRoleMenuDto));
    }

    @ApiOperation(value = "分页查询用户信息")
    @PostMapping("/pageGetSysUser")
    public BaseResponse pageGetSysUser(@RequestBody @ApiParam SysUserSecDto sysUserSecDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(sysUserSecDto),
                "分页查询用户信息"));
        return ResultUtil.success(sysUserService.pageGetSysUser(sysUserSecDto));
    }

    @ApiOperation(value = "分页查询角色信息")
    @PostMapping("/pageGetSysRole")
    public BaseResponse pageGetSysRole(@RequestBody @ApiParam SysRoleSecDto sysRoleSecDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(sysRoleSecDto),
                "分页查询角色信息"));
        return ResultUtil.success(sysUserService.pageGetSysRole(sysRoleSecDto));
    }
}
