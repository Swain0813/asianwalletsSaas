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
     * 删除指定ID与指定父ID的权限
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
}