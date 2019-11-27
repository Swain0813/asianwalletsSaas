package com.asianwallets.permissions.service;

import com.asianwallets.permissions.vo.FirstMenuVO;

import java.util.List;

public interface SysMenuService {


    /**
     * 查询用户所有权限信息
     *
     * @param userId         用户ID
     * @param permissionType 权限类型
     * @return 一级权限集合
     */
    List<FirstMenuVO> getAllMenuByUserId(String userId, Integer permissionType);

    /**
     * 查询角色所有权限信息
     *
     * @param roleId         角色ID
     * @param permissionType 权限类型
     * @return 一级权限集合
     */
    List<FirstMenuVO> getAllMenuByRoleId(String roleId, Integer permissionType);
}
