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
     * 删除指定权限ID集合的权限信息
     *
     * @param idList 权限ID集合
     * @return 修改条数
     */
    int deleteByMenuIdList(List<String> idList);

    /**
     * 根据权限类型查询所有权限信息
     *
     * @param permissionType 权限类型
     * @return 一级权限集合
     */
    List<FirstMenuVO> selectAllMenuByPermissionType(Integer permissionType);

    /**
     * 根据权限ID查询所有权限信息
     *
     * @param id 权限ID
     * @return 权限ID集合
     */
    FirstMenuVO selectAllMenuById(String id);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限集合
     */
    Set<String> selectMenuByUserId(String userId);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限集合
     */
    Set<String> selectMenuByRoleId(String roleId);

    /**
     * 根据父级ID查询权限ID信息
     *
     * @param parentId 父级ID
     * @return 权限集合
     */
    List<String> selectMenuByParentId(String parentId);

}