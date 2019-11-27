package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.SysMenu;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.dto.SysMenuDto;
import com.asianwallets.permissions.service.OperationLogService;
import com.asianwallets.permissions.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@Api(description = "权限接口")
@RequestMapping("/permission")
public class SysMenuController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private SysMenuService sysMenuService;

    @ApiOperation(value = "添加权限信息")
    @PostMapping("/addMenu")
    public BaseResponse addMenu(@RequestBody @ApiParam SysMenuDto sysMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysMenuDto),
                "添加权限信息"));
        return ResultUtil.success(sysMenuService.addMenu(getSysUserVO().getUsername(), sysMenuDto));
    }

    @ApiOperation(value = "删除权限信息")
    @PostMapping("/deleteMenu")
    public BaseResponse deleteMenu(@RequestBody @ApiParam SysMenuDto sysMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysMenuDto),
                "删除权限信息"));
        return ResultUtil.success(sysMenuService.deleteMenu(getSysUserVO().getUsername(), sysMenuDto));
    }

    @ApiOperation(value = "修改权限信息")
    @PostMapping("/updateMenu")
    public BaseResponse updateMenu(@RequestBody @ApiParam SysMenuDto sysMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysMenuDto),
                "修改权限信息"));
        return ResultUtil.success(sysMenuService.updateMenu(getSysUserVO().getUsername(), sysMenuDto));
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
}
