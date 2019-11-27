package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.dto.*;
import com.asianwallets.permissions.service.OperationLogService;
import com.asianwallets.permissions.service.SysMenuService;
import com.asianwallets.permissions.service.SysRoleService;
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

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuService sysMenuService;

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
    public BaseResponse pageGetSysUser(@RequestBody @ApiParam SysUserDto sysUserDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(sysUserDto),
                "分页查询用户信息"));
        return ResultUtil.success(sysUserService.pageGetSysUser(sysUserDto));
    }

    @ApiOperation(value = "分页查询角色信息")
    @PostMapping("/pageGetSysRole")
    public BaseResponse pageGetSysRole(@RequestBody @ApiParam SysRoleDto sysRoleDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(sysRoleDto),
                "分页查询角色信息"));
        return ResultUtil.success(sysUserService.pageGetSysRole(sysRoleDto));
    }

    @ApiOperation(value = "禁用/启用角色")
    @PostMapping("/banRole")
    public BaseResponse banRole(@RequestBody @ApiParam SysRoleDto sysRoleDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(sysRoleDto),
                "禁用/启用角色"));
        return ResultUtil.success(sysRoleService.banRole(getSysUserVO().getUsername(), sysRoleDto));
    }

    @ApiOperation(value = "重置密码")
    @GetMapping("/resetPassword")
    public BaseResponse resetPassword(@RequestParam @ApiParam String userId) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(userId),
                "重置密码"));
        return ResultUtil.success(sysUserService.resetPassword(getSysUserVO().getUsername(), userId));
    }

    @ApiOperation(value = "修改密码")
    @PostMapping("/updatePassword")
    public BaseResponse updatePassword(@RequestBody @ApiParam UpdatePasswordDto updatePasswordDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(getRequest().getParameterMap()),
                "修改密码"));
        return ResultUtil.success(sysUserService.updatePassword(getSysUserVO().getUsername(), updatePasswordDto));
    }

    @ApiOperation(value = "修改交易密码")
    @PostMapping("/updateTradePassword")
    public BaseResponse updateTradePassword(@RequestBody @ApiParam UpdatePasswordDto updatePasswordDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(getRequest().getParameterMap()),
                "修改交易密码"));
        return ResultUtil.success(sysUserService.updateTradePassword(getSysUserVO().getUsername(), updatePasswordDto));
    }

    @ApiOperation(value = "查询用户所有权限信息（userId可不传）")
    @GetMapping("/getAllMenuByUserId")
    public BaseResponse getAllMenuByUserId(@RequestParam(value = "userId", required = false) @ApiParam String userId,
                                           @RequestParam("permissionType") @ApiParam Integer permissionType) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSONObject.toJSONString(getRequest().getParameterMap()),
                "查询用户所有权限信息"));
        return ResultUtil.success(sysMenuService.getAllMenuByUserId(userId, permissionType));
    }

    @ApiOperation(value = "查询角色所有权限信息（roleId可不传）")
    @GetMapping("/getAllMenuByRoleId")
    public BaseResponse getAllMenuByRoleId(@RequestParam(value = "roleId", required = false) @ApiParam String roleId,
                                           @RequestParam("permissionType") @ApiParam Integer permissionType) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSONObject.toJSONString(getRequest().getParameterMap()),
                "查询角色所有权限信息"));
        return ResultUtil.success(sysMenuService.getAllMenuByRoleId(roleId, permissionType));
    }

    @ApiOperation(value = "查询用户详情")
    @GetMapping("/getSysUserDetail")
    public BaseResponse getSysUserDetail(@RequestParam @ApiParam String username) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSONObject.toJSONString(username),
                "查询用户详情"));
        return ResultUtil.success(sysUserService.getSysUserDetail(username));
    }

    @ApiOperation(value = "发送开户邮件")
    @PostMapping(value = "/sendInstitutionEmail")
    public BaseResponse sendInstitutionEmail(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSONObject.toJSONString(institutionDTO),
                "发送开户邮件"));
        institutionDTO.setLanguage(getLanguage());
        sysUserService.openAccountEmail(institutionDTO);
        return ResultUtil.success();
    }
}
