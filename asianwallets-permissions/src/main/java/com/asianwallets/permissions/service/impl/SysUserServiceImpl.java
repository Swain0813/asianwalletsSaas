package com.asianwallets.permissions.service.impl;

import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.entity.SysUser;
import com.asianwallets.common.entity.SysUserMenu;
import com.asianwallets.common.entity.SysUserRole;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.dao.SysUserMapper;
import com.asianwallets.permissions.dao.SysUserMenuMapper;
import com.asianwallets.permissions.dao.SysUserRoleMapper;
import com.asianwallets.permissions.dto.SysUserRoleDto;
import com.asianwallets.permissions.service.SysUserService;
import com.asianwallets.permissions.utils.BCryptUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户业务接口实现类
 */
@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserMenuMapper sysUserMenuMapper;

    @Autowired
    private AuditorProvider auditorProvider;

    /**
     * 根据用户名查询用户关联角色,权限信息
     *
     * @param userName 用户名
     * @return SysUserVO
     */
    public SysUserVO getSysUser(String userName) {
        return sysUserMapper.getSysUser(userName);
    }

    /**
     * 新增用户角色,用户权限信息
     *
     * @param username       用户名
     * @param sysUserRoleDto 用户角色输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addSysUser(String username, SysUserRoleDto sysUserRoleDto) {
        SysUser dbSysUser = sysUserMapper.getSysUserByUsername(sysUserRoleDto.getUsername());
        if (dbSysUser != null) {
            log.info("=========【新增用户角色,用户权限信息】==========【用户名已存在!】");
            throw new BusinessException(EResultEnum.USER_EXIST.getCode());
        }
        //新增角色
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserRoleDto, sysUser);
        sysUser.setId(IDS.uuid2());
        sysUser.setPassword(BCryptUtils.encode(sysUserRoleDto.getPassword()));
        sysUser.setTradePassword(BCryptUtils.encode(sysUserRoleDto.getTradePassword()));
        sysUser.setLanguage(auditorProvider.getLanguage());
        sysUser.setCreator(username);
        sysUser.setCreateTime(new Date());
        sysUser.setEnabled(true);
        //用户分配角色
        if (!ArrayUtil.isEmpty(sysUserRoleDto.getRoleIdList())) {
            List<SysUserRole> userRoleList = Lists.newArrayList();
            for (String roleId : sysUserRoleDto.getRoleIdList()) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setId(IDS.uuid2());
                sysUserRole.setUserId(sysUser.getId());
                sysUserRole.setRoleId(roleId);
                sysUserRole.setCreator(username);
                sysUserRole.setCreateTime(new Date());
                userRoleList.add(sysUserRole);
            }
            sysUserRoleMapper.insertList(userRoleList);
        }
        //用户分配权限
        if (!ArrayUtil.isEmpty(sysUserRoleDto.getMenuIdList())) {
            List<SysUserMenu> userMenuList = Lists.newArrayList();
            for (String menuId : sysUserRoleDto.getMenuIdList()) {
                SysUserMenu sysUserMenu = new SysUserMenu();
                sysUserMenu.setId(IDS.uuid2());
                sysUserMenu.setUserId(sysUser.getId());
                sysUserMenu.setMenuId(menuId);
                sysUserMenu.setCreator(username);
                sysUserMenu.setCreateTime(new Date());
                userMenuList.add(sysUserMenu);
            }
            sysUserMenuMapper.insertList(userMenuList);
        }
        return sysUserMapper.insert(sysUser);
    }

    /**
     * 修改用户角色,用户权限信息
     *
     * @param username       用户名
     * @param sysUserRoleDto 用户角色输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int updateSysUser(String username, SysUserRoleDto sysUserRoleDto) {
        SysUser dbSysUser = sysUserMapper.getSysUserByUsername(sysUserRoleDto.getUsername());
        if (dbSysUser == null) {
            log.info("=========【修改用户角色,用户权限信息】==========【用户名不存在!】");
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        //修改角色
        BeanUtils.copyProperties(sysUserRoleDto, dbSysUser);
        dbSysUser.setId(sysUserRoleDto.getUserId());
        dbSysUser.setUpdateTime(new Date());
        dbSysUser.setModifier(username);
        //用户分配角色
        sysUserRoleMapper.deleteByUserId(sysUserRoleDto.getUserId());
        if (!ArrayUtil.isEmpty(sysUserRoleDto.getRoleIdList())) {
            List<SysUserRole> userRoleList = Lists.newArrayList();
            for (String roleId : sysUserRoleDto.getRoleIdList()) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setId(IDS.uuid2());
                sysUserRole.setUserId(dbSysUser.getId());
                sysUserRole.setRoleId(roleId);
                sysUserRole.setCreator(username);
                sysUserRole.setCreateTime(new Date());
                userRoleList.add(sysUserRole);
            }
            sysUserRoleMapper.insertList(userRoleList);
        }
        //用户分配权限
        sysUserMenuMapper.deleteByUserId(sysUserRoleDto.getUserId());
        if (!ArrayUtil.isEmpty(sysUserRoleDto.getMenuIdList())) {
            List<SysUserMenu> userMenuList = Lists.newArrayList();
            for (String menuId : sysUserRoleDto.getMenuIdList()) {
                SysUserMenu sysUserMenu = new SysUserMenu();
                sysUserMenu.setId(IDS.uuid2());
                sysUserMenu.setUserId(dbSysUser.getId());
                sysUserMenu.setMenuId(menuId);
                sysUserMenu.setCreator(username);
                sysUserMenu.setCreateTime(new Date());
                userMenuList.add(sysUserMenu);
            }
            sysUserMenuMapper.insertList(userMenuList);
        }
        return sysUserMapper.updateByPrimaryKeySelective(dbSysUser);
    }
}
