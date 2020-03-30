package com.asianwallets.permissions.service.impl;

import com.asianwallets.common.entity.SysRole;
import com.asianwallets.common.entity.SysRoleMenu;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.permissions.dao.SysRoleMapper;
import com.asianwallets.permissions.dao.SysRoleMenuMapper;
import com.asianwallets.permissions.dto.SysRoleDto;
import com.asianwallets.permissions.dto.SysRoleMenuDto;
import com.asianwallets.permissions.service.SysRoleService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    /**
     * 新增角色权限信息
     *
     * @param username       用户名
     * @param sysRoleMenuDto 角色权限输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addSysRole(String username, SysRoleMenuDto sysRoleMenuDto) {
        SysRole dbSysRole = sysRoleMapper.getSysRoleByNameAndSysId(sysRoleMenuDto.getRoleName(), sysRoleMenuDto.getSysId());
        if (dbSysRole != null) {
            log.info("=========【新增角色权限信息】==========【角色名已存在!】");
            throw new BusinessException(EResultEnum.ROLE_EXIST.getCode());
        }
        SysRole sysRole = new SysRole();
        sysRole.setId(IDS.uuid2());
        sysRole.setSysId(sysRoleMenuDto.getSysId());
        sysRole.setPermissionType(sysRoleMenuDto.getPermissionType());
        sysRole.setRoleName(sysRoleMenuDto.getRoleName());
        sysRole.setDescription(sysRoleMenuDto.getDescription());
        sysRole.setCreator(username);
        sysRole.setCreateTime(new Date());
        sysRole.setSort(0);
        sysRole.setEnabled(true);
        List<SysRoleMenu> list = Lists.newArrayList();
        for (String menuId : sysRoleMenuDto.getMenuIdList()) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setId(IDS.uuid2());
            sysRoleMenu.setRoleId(sysRole.getId());
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setCreator(username);
            sysRoleMenu.setCreateTime(new Date());
            list.add(sysRoleMenu);
        }
        sysRoleMenuMapper.insertList(list);
        return sysRoleMapper.insert(sysRole);
    }

    /**
     * 修改角色权限信息
     *
     * @param username       用户名
     * @param sysRoleMenuDto 角色权限输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int updateSysRole(String username, SysRoleMenuDto sysRoleMenuDto) {
        SysRole dbSysRole = sysRoleMapper.selectByPrimaryKey(sysRoleMenuDto.getRoleId());
        if (dbSysRole == null) {
            log.info("=========【修改角色权限信息】==========【角色不存在!】");
            throw new BusinessException(EResultEnum.ROLE_NO_EXIST.getCode());
        }
        dbSysRole.setModifier(username);
        dbSysRole.setUpdateTime(new Date());
        sysRoleMapper.updateByPrimaryKeySelective(dbSysRole);
        //根据角色ID删除角色权限表信息
        sysRoleMenuMapper.deleteByRoleId(dbSysRole.getId());
        List<SysRoleMenu> roleMenuList = Lists.newArrayList();
        for (String menuId : sysRoleMenuDto.getMenuIdList()) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setId(IDS.uuid());
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(dbSysRole.getId());
            sysRoleMenu.setCreator(username);
            sysRoleMenu.setCreateTime(new Date());
            roleMenuList.add(sysRoleMenu);
        }
        if (roleMenuList.size() == 0) {
            log.info("=========【修改角色权限信息】==========【角色权限不能为空!】");
            throw new BusinessException(EResultEnum.ROLE_PERMISSION_IS_NOT_NULL.getCode());
        }
        return sysRoleMenuMapper.insertList(roleMenuList);
    }


    /**
     * 启用/禁用角色信息
     *
     * @param username   用户名
     * @param sysRoleDto 角色输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int banRole(String username, SysRoleDto sysRoleDto) {
        SysRole dbSysRole = sysRoleMapper.selectByPrimaryKey(sysRoleDto.getRoleId());
        if (dbSysRole == null) {
            log.info("=========【启用/禁用角色信息】==========【角色不存在!】");
            throw new BusinessException(EResultEnum.ROLE_NO_EXIST.getCode());
        }
        dbSysRole.setModifier(username);
        dbSysRole.setUpdateTime(new Date());
        dbSysRole.setEnabled(sysRoleDto.getEnabled());
        return sysRoleMapper.updateByPrimaryKeySelective(dbSysRole);
    }

    /**
     * 分页查询角色信息
     *
     * @param sysRoleDto 角色查询实体
     * @return 修改条数
     */
    @Override
    public PageInfo<SysRole> pageGetSysRole(SysRoleDto sysRoleDto) {
        List<SysRole> sysRoles = sysRoleMapper.pageGetSysRole(sysRoleDto);
        for (SysRole sysRole : sysRoles) {
            if (sysRole != null && !StringUtils.isEmpty(sysRole.getSysId())) {
                sysRole.setCreator(sysRole.getCreator().replace(sysRole.getSysId(), ""));
                if (!StringUtils.isEmpty(sysRole.getModifier())) {
                    sysRole.setModifier(sysRole.getModifier().replace(sysRole.getSysId(), ""));
                }
            }
        }
        return new PageInfo<>(sysRoles);
    }

}
