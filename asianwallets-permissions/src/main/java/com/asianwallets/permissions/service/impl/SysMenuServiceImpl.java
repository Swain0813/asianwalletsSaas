package com.asianwallets.permissions.service.impl;

import com.asianwallets.permissions.dao.SysMenuMapper;
import com.asianwallets.permissions.service.SysMenuService;
import com.asianwallets.permissions.vo.FirstMenuVO;
import com.asianwallets.permissions.vo.SecondMenuVO;
import com.asianwallets.permissions.vo.ThreeMenuVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

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
