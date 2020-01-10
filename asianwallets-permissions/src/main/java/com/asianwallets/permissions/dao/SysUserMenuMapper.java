package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysUserMenu;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 根据用户ID与权限ID启用禁用权限
     *
     * @param userId 用户ID
     * @param offId  权限ID
     * @return
     */
    int updateEnabledByUserIdAndMenuId(@Param("userId") String userId, @Param("offId") String offId, @Param("enabled") Boolean enabled);
}