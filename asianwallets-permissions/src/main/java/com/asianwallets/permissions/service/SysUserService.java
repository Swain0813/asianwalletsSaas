package com.asianwallets.permissions.service;

import com.asianwallets.common.vo.SysUserVO;

/**
 * 用户业务接口
 */
public interface SysUserService {

    /**
     * 根据用户名,权限类型查询用户关联角色,权限信息
     *
     * @param userName       用户名
     * @param sysId          系统ID
     * @param permissionType 权限类型
     * @return SysUserVO
     */
    SysUserVO getSysUser(String userName, String sysId, Integer permissionType);
}
