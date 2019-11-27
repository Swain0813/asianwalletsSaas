package com.asianwallets.permissions.service;

import com.asianwallets.permissions.dto.SysRoleDto;


public interface SysRoleService {

    /**
     * 启用/禁用角色信息
     *
     * @param username 用户名
     * @param sysRoleDto 角色输入实体
     * @return 修改条数
     */
    int banRole(String username, SysRoleDto sysRoleDto);

}
