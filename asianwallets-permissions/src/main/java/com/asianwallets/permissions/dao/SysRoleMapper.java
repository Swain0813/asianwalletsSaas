package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysRole;
import org.springframework.stereotype.Repository;


@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色名,系统ID
     *
     * @param roleName 角色名
     * @param sysId    系统ID
     * @return SysUserVO
     */
    SysRole getSysRoleByNameAndSysId(String roleName, String sysId);
}