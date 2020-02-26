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
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysUserRoleDto),
                "运营后台添加用户信息"));
        return ResultUtil.success(sysUserService.addSysUserByOperation(this.getSysUserVO().getUsername(), sysUserRoleDto));
    }

    @ApiOperation(value = "运营后台修改用户角色,用户权限信息")
    @PostMapping("/updateSysUserByOperation")
    public BaseResponse updateSysUserByOperation(@RequestBody @ApiParam SysUserRoleDto sysUserRoleDto) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(sysUserRoleDto),
                "运营后台修改用户信息"));
        return ResultUtil.success(sysUserService.updateSysUserByOperation(this.getSysUserVO().getUsername(), sysUserRoleDto));
    }

    @ApiOperation(value = "机构后台添加用户角色,用户权限信息")
    @PostMapping("/addSysUserByInstitution")
    public BaseResponse addSysUserByInstitution(@RequestBody @ApiParam SysUserRoleDto sysUserRoleDto) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysUserRoleDto),
                "机构后台添加用户角色"));
        return ResultUtil.success(sysUserService.addSysUserByInstitution(this.getSysUserVO().getUsername(), sysUserRoleDto));
    }

    @ApiOperation(value = "机构后台修改用户角色,用户权限信息")
    @PostMapping("/updateSysUserByInstitution")
    public BaseResponse updateSysUserByInstitution(@RequestBody @ApiParam SysUserRoleDto sysUserRoleDto) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(sysUserRoleDto),
                "机构后台修改用户角色"));
        return ResultUtil.success(sysUserService.updateSysUserByInstitution(this.getSysUserVO().getUsername(), sysUserRoleDto));
    }

    @ApiOperation(value = "重置密码并发送邮件")
    @GetMapping("/resetPassword")
    public BaseResponse resetPassword(@RequestParam @ApiParam String userId) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(userId),
                "重置密码并发送邮件"));
        return ResultUtil.success(sysUserService.resetPassword(this.getSysUserVO().getUsername(), userId));
    }

    @ApiOperation(value = "重置密码成初始密码")
    @GetMapping("/resetPassword")
    public BaseResponse resetPwd(@RequestParam @ApiParam String userId) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(userId),
                "重置密码成初始密码"));
        return ResultUtil.success(sysUserService.resetPwd(this.getSysUserVO().getUsername(), userId));
    }

    @ApiOperation(value = "修改密码")
    @PostMapping("/updatePassword")
    public BaseResponse updatePassword(@RequestBody @ApiParam UpdatePasswordDto updatePasswordDto) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(getRequest().getParameterMap()),
                "修改密码"));
        return ResultUtil.success(sysUserService.updatePassword(this.getSysUserVO().getUsername(), updatePasswordDto));
    }

    @ApiOperation(value = "修改交易密码")
    @PostMapping("/updateTradePassword")
    public BaseResponse updateTradePassword(@RequestBody @ApiParam UpdatePasswordDto updatePasswordDto) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(getRequest().getParameterMap()),
                "修改交易密码"));
        return ResultUtil.success(sysUserService.updateTradePassword(this.getSysUserVO().getUsername(), updatePasswordDto));
    }

    @ApiOperation(value = "查询用户详情")
    @GetMapping("/getSysUserDetail")
    public BaseResponse getSysUserDetail(@RequestParam @ApiParam String username) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSONObject.toJSONString(username),
                "查询用户详情"));
        return ResultUtil.success(sysUserService.getSysUserDetail(username));
    }

    @ApiOperation(value = "分页查询用户信息")
    @PostMapping("/pageGetSysUser")
    public BaseResponse pageGetSysUser(@RequestBody @ApiParam SysUserDto sysUserDto) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(sysUserDto),
                "分页查询用户信息"));
        return ResultUtil.success(sysUserService.pageGetSysUser(sysUserDto));
    }

    @ApiOperation(value = "发送开户邮件")
    @PostMapping(value = "/sendInstitutionEmail")
    public BaseResponse sendInstitutionEmail(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSONObject.toJSONString(institutionDTO),
                "发送开户邮件"));
        institutionDTO.setLanguage(getLanguage());
        sysUserService.openAccountEmail(institutionDTO);
        return ResultUtil.success();
    }

}
