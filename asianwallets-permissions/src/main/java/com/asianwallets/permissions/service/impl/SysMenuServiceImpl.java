package com.asianwallets.permissions.service.impl;

import java.util.Date;

import com.asianwallets.common.entity.SysMenu;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.permissions.dao.SysMenuMapper;
import com.asianwallets.permissions.dto.FirstMenuDto;
import com.asianwallets.permissions.dto.SecondMenuDto;
import com.asianwallets.permissions.dto.SysMenuDto;
import com.asianwallets.permissions.dto.ThreeMenuDto;
import com.asianwallets.permissions.service.SysMenuService;
import com.asianwallets.permissions.vo.FirstMenuVO;
import com.asianwallets.permissions.vo.SecondMenuVO;
import com.asianwallets.permissions.vo.ThreeMenuVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    /**
     * 添加权限信息
     *
     * @param username     用户名
     * @param firstMenuDto 权限输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addMenu(String username, FirstMenuDto firstMenuDto) {
        List<SysMenu> menuList = new ArrayList<>();
        SysMenu firstMenu = new SysMenu();
        firstMenu.setId(IDS.uuid2());
        firstMenu.setLevel(0);
        firstMenu.setEnName(firstMenuDto.getEName());
        firstMenu.setCnName(firstMenuDto.getCName());
        firstMenu.setPermissionType(firstMenuDto.getPermissionType());
        firstMenu.setDescription(firstMenuDto.getDescription());
        firstMenu.setSort(0);
        firstMenu.setCreateTime(new Date());
        firstMenu.setCreator(username);
        firstMenu.setEnabled(true);
        menuList.add(firstMenu);
        for (SecondMenuDto secondMenuDto : firstMenuDto.getSecondMenuDtoList()) {
            SysMenu secondMenu = new SysMenu();
            secondMenu.setId(IDS.uuid2());
            secondMenu.setLevel(1);
            secondMenu.setParentId(firstMenu.getId());
            secondMenu.setEnName(secondMenuDto.getEName());
            secondMenu.setCnName(secondMenuDto.getCName());
            secondMenu.setPermissionType(secondMenuDto.getPermissionType());
            secondMenu.setDescription(secondMenuDto.getDescription());
            secondMenu.setSort(0);
            secondMenu.setCreateTime(new Date());
            secondMenu.setCreator(username);
            secondMenu.setEnabled(true);
            menuList.add(secondMenu);
            for (ThreeMenuDto threeMenuDto : secondMenuDto.getThreeMenuDtoList()) {
                SysMenu threeMenu = new SysMenu();
                threeMenu.setId(IDS.uuid2());
                threeMenu.setLevel(2);
                threeMenu.setParentId(secondMenu.getId());
                threeMenu.setEnName(threeMenuDto.getEName());
                threeMenu.setCnName(threeMenuDto.getCName());
                threeMenu.setPermissionType(threeMenuDto.getPermissionType());
                threeMenu.setDescription(threeMenuDto.getDescription());
                threeMenu.setSort(0);
                threeMenu.setCreateTime(new Date());
                threeMenu.setCreator(username);
                threeMenu.setEnabled(true);
                menuList.add(threeMenu);
            }
        }
        return sysMenuMapper.insertList(menuList);
    }

    /**
     * 删除权限信息
     *
     * @param username   用户名
     * @param sysMenuDto 权限输入实体
     * @return 修改条数
     */
    @Override
    public int deleteMenu(String username, SysMenuDto sysMenuDto) {
        return 0;
    }

    /**
     * 修改权限信息
     *
     * @param username   用户名
     * @param sysMenuDto 权限输入实体
     * @return 修改条数
     */
    @Override
    public int updateMenu(String username, SysMenuDto sysMenuDto) {
        return 0;
    }

    /**
     * 查询用户所有权限信息
     *
     * @param userId         用户ID
     * @param permissionType 权限类型
     * @return 一级权限集合
     */
    @Override
    public List<FirstMenuVO> getAllMenuByUserId(String userId, Integer permissionType) {
        List<FirstMenuVO> menuList = sysMenuMapper.getAllMenuByPermissionType(permissionType);
        if (StringUtils.isNotBlank(userId)) {
            Set<String> menuSet = sysMenuMapper.getUserMenu(userId);
            for (FirstMenuVO firstMenuVO : menuList) {
                if (menuSet.contains(firstMenuVO.getId())) {
                    firstMenuVO.setFlag(true);
                }
                for (SecondMenuVO secondMenuVO : firstMenuVO.getSecondMenuVOS()) {
                    if (menuSet.contains(secondMenuVO.getId())) {
                        secondMenuVO.setFlag(true);
                    }
                    for (ThreeMenuVO threeMenuVO : secondMenuVO.getThreeMenuVOS()) {
                        if (menuSet.contains(threeMenuVO.getId())) {
                            threeMenuVO.setFlag(true);
                        }
                    }
                }
            }
        }
        return menuList;
    }

    /**
     * 查询角色所有权限信息
     *
     * @param roleId         角色ID
     * @param permissionType 权限类型
     * @return 一级权限集合
     */
    @Override
    public List<FirstMenuVO> getAllMenuByRoleId(String roleId, Integer permissionType) {
        //根据权限类型查询所有权限
        List<FirstMenuVO> menuList = sysMenuMapper.getAllMenuByPermissionType(permissionType);
        if (StringUtils.isNotBlank(roleId)) {
            Set<String> menuSet = sysMenuMapper.getRoleMenu(roleId);
            for (FirstMenuVO firstMenuVO : menuList) {
                if (menuSet.contains(firstMenuVO.getId())) {
                    firstMenuVO.setFlag(true);
                }
                for (SecondMenuVO secondMenuVO : firstMenuVO.getSecondMenuVOS()) {
                    if (menuSet.contains(secondMenuVO.getId())) {
                        secondMenuVO.setFlag(true);
                    }
                    for (ThreeMenuVO threeMenuVO : secondMenuVO.getThreeMenuVOS()) {
                        if (menuSet.contains(threeMenuVO.getId())) {
                            threeMenuVO.setFlag(true);
                        }
                    }
                }
            }
        }
        return menuList;
    }
}
