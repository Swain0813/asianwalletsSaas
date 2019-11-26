package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysUserMenu;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserMenuMapper extends BaseMapper<SysUserMenu> {

    /**
     * 根据用户id删除用户权限中间表
     *
     * @param userId 用户ID
     * @return 删除条数
     */
    int deleteByUserId(String userId);

}