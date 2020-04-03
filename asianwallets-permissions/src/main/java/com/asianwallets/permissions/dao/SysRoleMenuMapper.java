package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

    /**
     * 根据角色ID与权限ID启用禁用权限
     *
     * @param roleId 角色ID
     * @param offId  权限ID
     * @return
     */
    int updateEnabledByRoleIdAndMenuId(@Param("roleId") String roleId, @Param("offId") String offId, @Param("enabled") Boolean enabled);


    /**
     * 根据权限类型查询权限id
     * @param type
     * @return
     */
    @Select("select id from sys_menu where permission_type = #{type}")
    List<String> getMenuId(String type);

}