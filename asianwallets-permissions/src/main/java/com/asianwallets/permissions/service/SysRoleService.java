package com.asianwallets.permissions.service;

import com.asianwallets.common.entity.SysRole;
import com.asianwallets.permissions.dto.SysRoleDto;
import com.asianwallets.permissions.dto.SysRoleMenuDto;
import com.github.pagehelper.PageInfo;


public interface SysRoleService {

    /**
     * 新增角色权限信息
     *
     * @param username       用户名
     * @param sysRoleMenuDto 角色权限输入实体
     * @return 修改条数
     */
    int addSysRole(String username, SysRoleMenuDto sysRoleMenuDto);

    /**
     * 修改角色权限信息
     *
     * @param username       用户名
     * @param sysRoleMenuDto 角色权限输入实体
     * @return 修改条数
     */
    int updateSysRole(String username, SysRoleMenuDto sysRoleMenuDto);

    /**
     * 分页查询角色信息
     *
     * @param sysRoleSecDto 角色权限输入实体
     * @return 修改条数
     */
    PageInfo<SysRole> pageGetSysRole(SysRoleDto sysRoleSecDto);

    /**
     * 启用/禁用角色信息
     *
     * @param username 用户名
     * @param sysRoleDto 角色输入实体
     * @return 修改条数
     */
    int banRole(String username, SysRoleDto sysRoleDto);


    /**
     * 给角色加管理员权限
     * @param roldId
     * @param type
     * @return
     */
    int setAdmin(String roldId,String type);

}
