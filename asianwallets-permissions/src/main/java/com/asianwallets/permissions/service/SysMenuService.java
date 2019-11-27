package com.asianwallets.permissions.service;

import com.asianwallets.permissions.dto.SysMenuDto;
import com.asianwallets.permissions.vo.FirstMenuVO;

import java.util.List;

public interface SysMenuService {

    /**
     * 添加权限信息
     *
     * @param username   用户名
     * @param sysMenuDto 权限输入实体
     * @return 修改条数
     */
    int addMenu(String username, SysMenuDto sysMenuDto);

    /**
     * 删除权限信息
     *
     * @param username   用户名
     * @param sysMenuDto 权限输入实体
     * @return 修改条数
     */
    int deleteMenu(String username, SysMenuDto sysMenuDto);

    /**
     * 修改权限信息
     *
     * @param username   用户名
     * @param sysMenuDto 权限输入实体
     * @return 修改条数
     */
    int updateMenu(String username, SysMenuDto sysMenuDto);


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
