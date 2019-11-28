package com.asianwallets.permissions.service;

import com.asianwallets.permissions.dto.FirstMenuDto;
import com.asianwallets.permissions.dto.SecondMenuDto;
import com.asianwallets.permissions.dto.SysMenuDto;
import com.asianwallets.permissions.dto.ThreeMenuDto;
import com.asianwallets.permissions.vo.FirstMenuVO;

import java.util.List;

public interface SysMenuService {

    /**
     * 添加一二三级菜单权限信息
     *
     * @param username     用户名
     * @param firstMenuDto 一级权限输入实体
     * @return 修改条数
     */
    int addThreeLayerMenu(String username, FirstMenuDto firstMenuDto);

    /**
     * 添加二三级菜单权限信息
     *
     * @param username      用户名
     * @param secondMenuDto 二级权限输入实体
     * @return 修改条数
     */
    int addTwoLayerMenu(String username, SecondMenuDto secondMenuDto);

    /**
     * 添加三级菜单权限信息
     *
     * @param username     用户名
     * @param threeMenuDto 三级权限输入实体
     * @return 修改条数
     */
    int addOneLayerMenu(String username, ThreeMenuDto threeMenuDto);

    /**
     * 删除权限信息
     *
     * @param menuId   权限ID
     * @return 修改条数
     */
    int deleteMenu(String menuId);

    /**
     * 修改权限信息
     *
     * @param username   用户名
     * @param sysMenuDto 权限输入实体
     * @return 修改条数
     */
    int updateMenu(String username, SysMenuDto sysMenuDto);


    /**
     * 查询用户所有权限信息
     *
     * @param userId         用户ID
     * @param permissionType 权限类型
     * @return 一级权限集合
     */
    List<FirstMenuVO> getAllMenuByUserId(String userId, Integer permissionType);

    /**
     * 查询角色所有权限信息
     *
     * @param roleId         角色ID
     * @param permissionType 权限类型
     * @return 一级权限集合
     */
    List<FirstMenuVO> getAllMenuByRoleId(String roleId, Integer permissionType);

}
