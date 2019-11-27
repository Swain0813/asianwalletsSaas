package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysRoleMenu;
import org.springframework.stereotype.Repository;


@Repository
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色id删除角色权限中间表
     *
     * @param roleId 角色id
     * @return 修改条数
     */
    int deleteByRoleId(String roleId);
}