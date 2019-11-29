package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysUserMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserMenuMapper extends BaseMapper<SysUserMenu> {

    /**
     * 根据用户id删除用户权限中间表
     *
     * @param userId 用户ID
     * @return 删除条数
     */
    int deleteByUserId(String userId);

    /**
     * 根据权限id删除用户权限中间表
     *
     * @param menuId 用户ID
     * @return 删除条数
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