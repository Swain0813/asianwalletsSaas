package com.asianwallets.permissions.service;

import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.dto.SysUserRoleDto;

/**
 * 用户业务接口
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户关联角色,权限信息
     *
     * @param userName 用户名
     * @return SysUserVO
     */
    SysUserVO getSysUser(String userName);

    /**
     * 新增用户角色,用户权限信息
     *
     * @param username 用户名
     * @param sysUserRoleDto 用户角色输入实体
     * @return 修改条数
     */
    int addSysUser(String username, SysUserRoleDto sysUserRoleDto);
}
