package com.asianwallets.permissions.service.impl;

import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.dao.*;
import com.asianwallets.permissions.dto.SysRoleDto;
import com.asianwallets.permissions.dto.SysRoleMenuDto;
import com.asianwallets.permissions.dto.SysUserDto;
import com.asianwallets.permissions.dto.SysUserRoleDto;
import com.asianwallets.permissions.feign.message.MessageFeign;
import com.asianwallets.permissions.service.SysUserService;
import com.asianwallets.permissions.utils.BCryptUtils;
import com.asianwallets.permissions.vo.SysUserSecVO;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户业务接口实现类
 */
@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

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
     * 分配用户角色,用户权限信息
     *
     * @param sysUser        用户实体
     * @param sysUserRoleDto 用户角色输入实体
     */
    private void allotSysRoleAndSysMenu(SysUser sysUser, SysUserRoleDto sysUserRoleDto) {
        //用户分配角色
        if (!ArrayUtil.isEmpty(sysUserRoleDto.getRoleIdList())) {
            List<SysUserRole> userRoleList = Lists.newArrayList();
            for (String roleId : sysUserRoleDto.getRoleIdList()) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setId(IDS.uuid2());
                sysUserRole.setUserId(sysUser.getId());
                sysUserRole.setRoleId(roleId);
                sysUserRole.setCreator(sysUser.getCreator());
                sysUserRole.setModifier(sysUser.getCreator());
                sysUserRole.setCreateTime(new Date());
                sysUserRole.setUpdateTime(new Date());
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
                sysUserMenu.setCreator(sysUser.getCreator());
                sysUserMenu.setMenuId(sysUser.getCreator());
                sysUserMenu.setCreateTime(new Date());
                sysUserMenu.setUpdateTime(new Date());
                userMenuList.add(sysUserMenu);
            }
            sysUserMenuMapper.insertList(userMenuList);
        }
    }

    /**
     * 运营后台新增用户角色,用户权限信息
     *
     * @param username       用户名
     * @param sysUserRoleDto 用户角色输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addSysUserByOperation(String username, SysUserRoleDto sysUserRoleDto) {
        SysUser dbSysUser = sysUserMapper.getSysUserByUsername(sysUserRoleDto.getUsername());
        if (dbSysUser != null) {
            log.info("=========【运营后台新增用户角色,用户权限信息】==========【用户名已存在!】");
            throw new BusinessException(EResultEnum.USER_EXIST.getCode());
        }
        //新增角色
        SysUser sysUser = new SysUser();
        sysUser.setId(IDS.uuid2());
        sysUser.setPassword(BCryptUtils.encode(sysUserRoleDto.getPassword()));
        sysUser.setTradePassword(BCryptUtils.encode(sysUserRoleDto.getTradePassword()));
        sysUser.setLanguage(auditorProvider.getLanguage());
        sysUser.setName(sysUserRoleDto.getName());
        sysUser.setEmail(sysUserRoleDto.getEmail());
        sysUser.setCreator(username);
        sysUser.setCreateTime(new Date());
        sysUser.setEnabled(true);
        //分配用户角色,用户权限信息
        allotSysRoleAndSysMenu(sysUser, sysUserRoleDto);
        return sysUserMapper.insert(sysUser);
    }

    /**
     * 运营后台修改用户角色,用户权限信息
     *
     * @param username       用户名
     * @param sysUserRoleDto 用户角色输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int updateSysUserByOperation(String username, SysUserRoleDto sysUserRoleDto) {
        SysUser dbSysUser = sysUserMapper.getSysUserByUsername(sysUserRoleDto.getUsername());
        if (dbSysUser == null) {
            log.info("=========【运营后台修改用户角色,用户权限信息】==========【用户名不存在!】");
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        //修改角色
        BeanUtils.copyProperties(sysUserRoleDto, dbSysUser);
        dbSysUser.setId(sysUserRoleDto.getUserId());
        dbSysUser.setUpdateTime(new Date());
        dbSysUser.setModifier(username);
        //删除用户角色表中的信息
        sysUserRoleMapper.deleteByUserId(sysUserRoleDto.getUserId());
        //删除用户权限表中的信息
        sysUserMenuMapper.deleteByUserId(sysUserRoleDto.getUserId());
        //分配用户角色,用户权限信息
        allotSysRoleAndSysMenu(dbSysUser, sysUserRoleDto);
        return sysUserMapper.updateByPrimaryKeySelective(dbSysUser);
    }


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
            sysRoleMenu.setId(IDS.uuid());
            sysRoleMenu.setRoleId(sysRole.getId());
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setCreator(username);
            sysRoleMenu.setModifier(username);
            sysRoleMenu.setCreateTime(new Date());
            sysRoleMenu.setUpdateTime(new Date());
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
            sysRoleMenu.setModifier(username);
            sysRoleMenu.setCreateTime(new Date());
            sysRoleMenu.setUpdateTime(new Date());
            roleMenuList.add(sysRoleMenu);
        }
        if (roleMenuList.size() == 0) {
            log.info("=========【修改角色权限信息】==========【角色权限不能为空!】");
            throw new BusinessException(EResultEnum.ROLE_PERMISSION_IS_NOT_NULL.getCode());
        }
        return sysRoleMenuMapper.insertList(roleMenuList);
    }

    /**
     * 分页查询用户信息
     *
     * @param sysUserDto 用户查询实体
     * @return 修改条数
     */
    @Override
    public PageInfo<SysUserSecVO> pageGetSysUser(SysUserDto sysUserDto) {
        List<SysUserSecVO> sysUserList = new ArrayList<>();
        if (AsianWalletConstant.OPERATION.equals(sysUserDto.getPermissionType())) {
            //运营系统
            sysUserDto.setSort("s.create_time");
            sysUserList = sysUserMapper.pageGetSysUserByOperation(sysUserDto);
        }
        return new PageInfo<>(sysUserList);
    }

    /**
     * 分页查询角色信息
     *
     * @param sysRoleDto 角色查询实体
     * @return 修改条数
     */
    @Override
    public PageInfo<SysRole> pageGetSysRole(SysRoleDto sysRoleDto) {
        return new PageInfo<>(sysRoleMapper.pageGetSysRole(sysRoleDto));
    }

    /**
     * 机构开户后发送邮件
     *
     * @param institutionDTO
     */
    @Override
    public void openAccountEamin(InstitutionDTO institutionDTO) {
        log.info("*********************开户发送邮件 Start*************************************");
        try {
            if (!StringUtils.isEmpty(institutionDTO.getInstitutionEmail())) {
                log.info("*******************发送的机构邮箱是：*******************", institutionDTO.getInstitutionEmail());
                Map<String, Object> map = new HashMap<>();
                SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd");//日期格式
                map.put("dateTime", sf.format(new Date()));//发送日期
                map.put("institutionName", institutionDTO.getCnName());//机构名称
                map.put("institutionCode", institutionDTO.getInstitutionId());//机构code
                messageFeign.sendTemplateMail(institutionDTO.getInstitutionEmail(), institutionDTO.getLanguage(), Status._3, map);
            }
        } catch (Exception e) {
            log.error("开户发送邮件失败：{}==={}", institutionDTO.getInstitutionEmail(), e.getMessage());
        }
        log.info("*********************开户发送邮件 End*************************************");
    }
}
