package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.dto.*;
import com.asianwallets.permissions.service.OperationLogService;
import com.asianwallets.permissions.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@Api(description = "角色接口")
@RequestMapping("/permission")
public class SysRoleController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation(value = "添加角色权限信息")
    @PostMapping("/addSysRole")
    public BaseResponse addSysRole(@RequestBody @ApiParam SysRoleMenuDto sysRoleMenuDto) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName(), AsianWalletConstant.ADD, JSON.toJSONString(sysRoleMenuDto),
                "添加角色权限信息"));
        return ResultUtil.success(sysRoleService.addSysRole(this.getUserName(), sysRoleMenuDto));
    }

    @ApiOperation(value = "修改角色权限信息")
    @PostMapping("/updateSysRole")
    public BaseResponse updateSysRole(@RequestBody @ApiParam SysRoleMenuDto sysRoleMenuDto) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(sysRoleMenuDto),
                "修改角色权限信息"));
        return ResultUtil.success(sysRoleService.updateSysRole(this.getUserName(), sysRoleMenuDto));
    }

    @ApiOperation(value = "分页查询角色信息")
    @PostMapping("/pageGetSysRole")
    public BaseResponse pageGetSysRole(@RequestBody @ApiParam SysRoleDto sysRoleDto) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(sysRoleDto),
                "分页查询角色信息"));
        return ResultUtil.success(sysRoleService.pageGetSysRole(sysRoleDto));
    }

    @ApiOperation(value = "禁用/启用角色")
    @PostMapping("/banRole")
    public BaseResponse banRole(@RequestBody @ApiParam SysRoleDto sysRoleDto) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(sysRoleDto),
                "禁用/启用角色"));
        return ResultUtil.success(sysRoleService.banRole(this.getUserName(), sysRoleDto));
    }

}
