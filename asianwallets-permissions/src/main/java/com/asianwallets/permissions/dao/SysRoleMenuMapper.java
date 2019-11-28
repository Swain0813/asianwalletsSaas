package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysRoleMenu;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色id删除角色权限中间表
     *
     * @param roleId 角色id
     * @return 修改条数
     */
    int deleteByRoleId(String roleId);

    /**
     * 根据权限id删除角色权限中间表
     *
     * @param menuId 权限id
     * @return 修改条数
     */
    int deleteByMenuId(String menuId);

    /**
     * 删除指定权限ID集合的权限信息
     *
     * @param menuIdList 权限ID集合
     * @return 修改条数
     */
    int deleteByMenuIdList(List<String> menuIdList);
}