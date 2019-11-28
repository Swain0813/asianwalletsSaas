package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysMenu;
import com.asianwallets.permissions.vo.FirstMenuVO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 删除ID与父ID为指定权限ID集合的权限信息
     *
     * @param menuIdList 权限ID集合
     * @return 修改条数
     */
    int deleteByIdAndParentIdList(List<String> menuIdList);

    /**
     * 删除ID与父ID为指定权限ID的权限信息
     *
     * @param menuId 权限ID
     * @return 修改条数
     */
    int deleteByIdAndParentId(String menuId);

    /**
     * 查询所有权限
     *
     * @param permissionType 权限类型
     * @return 一级权限集合
     */
    List<FirstMenuVO> getAllMenuByPermissionType(Integer permissionType);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限集合
     */
    Set<String> getUserMenu(String userId);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限集合
     */
    Set<String> getRoleMenu(String roleId);

    /**
     * 根据父级ID查询权限ID信息
     *
     * @param parentId 父级ID
     * @return 权限集合
     */
    List<String> getMenuByParentId(String parentId);
}