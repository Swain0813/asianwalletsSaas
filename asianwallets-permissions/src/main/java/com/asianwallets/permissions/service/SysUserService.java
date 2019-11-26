package com.asianwallets.permissions.service;

import com.asianwallets.common.vo.SysUserVO;

/**
 * 用户业务接口
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户关联角色,权限信息
     *
     * @param userName       用户名
     * @return SysUserVO
     */
    SysUserVO getSysUser(String userName);
}
