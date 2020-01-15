package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.dto.*;
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

    @ApiOperation(value = "添加一二三级菜单权限信息")
    @PostMapping("/addThreeLayerMenu")
    public BaseResponse addThreeLayerMenu(@RequestBody @ApiParam FirstMenuDto firstMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(firstMenuDto),
                "添加一二三级菜单权限信息"));
        return ResultUtil.success(sysMenuService.addThreeLayerMenu(getSysUserVO().getUsername(), firstMenuDto));
    }

    @ApiOperation(value = "添加二三级菜单权限信息")
    @PostMapping("/addTwoLayerMenu")
    public BaseResponse addTwoLayerMenu(@RequestBody @ApiParam SecondMenuDto secondMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(secondMenuDto),
                "添加二三级菜单权限信息"));
        return ResultUtil.success(sysMenuService.addTwoLayerMenu(getSysUserVO().getUsername(), secondMenuDto));
    }

    @ApiOperation(value = "添加三级菜单权限信息")
    @PostMapping("/addOneLayerMenu")
    public BaseResponse addOneLayerMenu(@RequestBody @ApiParam ThreeMenuDto threeMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(threeMenuDto),
                "添加三级菜单权限信息"));
        return ResultUtil.success(sysMenuService.addOneLayerMenu(getSysUserVO().getUsername(), threeMenuDto));
    }

    @ApiOperation(value = "添加菜单权限信息")
    @PostMapping("/addMenu")
    public BaseResponse addMenu(@RequestBody @ApiParam SysMenuDto sysMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(sysMenuDto),
                "添加菜单权限信息"));
        return ResultUtil.success(sysMenuService.addMenu(getSysUserVO().getUsername(), sysMenuDto));
    }

    @ApiOperation(value = "删除权限信息")
    @GetMapping("/deleteMenu")
    public BaseResponse deleteMenu(@RequestParam @ApiParam String menuId) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.DELETE, JSON.toJSONString(menuId),
                "删除权限信息"));
        return ResultUtil.success(sysMenuService.deleteMenu(menuId));
    }

    @ApiOperation(value = "修改权限信息")
    @PostMapping("/updateMenu")
    public BaseResponse updateMenu(@RequestBody @ApiParam SysMenuDto sysMenuDto) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(sysMenuDto),
                "修改权限信息"));
        return ResultUtil.success(sysMenuService.updateMenu(getSysUserVO().getUsername(), sysMenuDto));
    }

    @ApiOperation(value = "查询用户所有权限信息(userId可不传)")
    @GetMapping("/getAllMenuByUserId")
    public BaseResponse getAllMenuByUserId(@RequestParam(value = "userId", required = false) @ApiParam String userId,
                                           @RequestParam("permissionType") @ApiParam Integer permissionType) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSONObject.toJSONString(getRequest().getParameterMap()),
                "查询用户所有权限信息"));
        return ResultUtil.success(sysMenuService.getAllMenuByUserId(userId, permissionType));
    }

    @ApiOperation(value = "查询角色所有权限信息(roleId可不传)")
    @GetMapping("/getAllMenuByRoleId")
    public BaseResponse getAllMenuByRoleId(@RequestParam(value = "roleId", required = false) @ApiParam String roleId,
                                           @RequestParam("permissionType") @ApiParam Integer permissionType) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSONObject.toJSONString(getRequest().getParameterMap()),
                "查询角色所有权限信息"));
        return ResultUtil.success(sysMenuService.getAllMenuByRoleId(roleId, permissionType));
    }

    @ApiOperation(value = "查询用户所有权限信息(前端用)")
    @GetMapping("/getAllMenu")
    public BaseResponse getAllMenu(@RequestParam(value = "userId", required = false) @ApiParam String userId,
                                   @RequestParam("permissionType") @ApiParam Integer permissionType) {
        return ResultUtil.success(sysMenuService.getAllMenuByUserId(userId, permissionType));
    }

    @ApiOperation(value = "修改机构,商户,代理,pos权限")
    @PostMapping("/updateInsPermission")
    public BaseResponse updateInsPermission(@RequestBody @ApiParam UpdateInsPermissionDto updateInsPermissionDto) {
        return ResultUtil.success(sysMenuService.updateInsPermission(getSysUserVO().getUsername(), updateInsPermissionDto));
    }

    @ApiOperation(value = "查询机构,商户,代理,pos权限")
    @PostMapping("/getInsPermission")
    public BaseResponse getInsPermission(@RequestBody @ApiParam UpdateInsPermissionDto updateInsPermissionDto) {
        return ResultUtil.success(sysMenuService.getInsPermission(updateInsPermissionDto));
    }
}
