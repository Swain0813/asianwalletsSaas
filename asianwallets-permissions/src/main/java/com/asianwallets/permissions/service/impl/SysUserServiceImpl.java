package com.asianwallets.permissions.service.impl;

import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResPermissions;
import com.asianwallets.common.response.ResRole;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.SysMenuVO;
import com.asianwallets.common.vo.SysRoleVO;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.dao.*;
import com.asianwallets.permissions.dto.*;
import com.asianwallets.permissions.dto.SysUserDto;
import com.asianwallets.permissions.dto.SysUserRoleDto;
import com.asianwallets.permissions.feign.base.InstitutionFeign;
import com.asianwallets.permissions.feign.message.MessageFeign;
import com.asianwallets.permissions.service.SysUserService;
import com.asianwallets.common.utils.BCryptUtils;
import com.asianwallets.permissions.vo.SysUserDetailVO;
import com.asianwallets.permissions.vo.SysUserSecVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
@Transactional
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private InstitutionFeign institutionFeign;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserMenuMapper sysUserMenuMapper;

    @Autowired
    private AuditorProvider auditorProvider;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 创建用户
     *
     * @param username       用户名
     * @param sysUserRoleDto 用户角色输入实体
     * @return 用户
     */
    private SysUser createSysUser(String username, SysUserRoleDto sysUserRoleDto) {
        SysUser sysUser = new SysUser();
        sysUser.setId(IDS.uuid2());
        sysUser.setPassword(BCryptUtils.encode(sysUserRoleDto.getPassword()));
        sysUser.setTradePassword(BCryptUtils.encode(sysUserRoleDto.getTradePassword()));
        sysUser.setLanguage(auditorProvider.getLanguage());
        sysUser.setPermissionType(sysUserRoleDto.getPermissionType());
        sysUser.setName(sysUserRoleDto.getName());
        sysUser.setEmail(sysUserRoleDto.getEmail());
        sysUser.setCreator(username);
        sysUser.setCreateTime(new Date());
        return sysUser;
    }

    /**
     * 分配用户角色,用户权限信息
     *
     * @param sysUser        用户实体
     * @param sysUserRoleDto 用户角色输入实体
     */
    private void allotSysRoleAndSysMenu(String username, SysUser sysUser, SysUserRoleDto sysUserRoleDto) {
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
        //创建角色
        SysUser sysUser = createSysUser(username, sysUserRoleDto);
        sysUser.setUsername(sysUserRoleDto.getUsername());
        //分配用户角色,用户权限信息
        allotSysRoleAndSysMenu(username, sysUser, sysUserRoleDto);
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
        allotSysRoleAndSysMenu(username, dbSysUser, sysUserRoleDto);
        return sysUserMapper.updateByPrimaryKeySelective(dbSysUser);
    }

    /**
     * 机构后台新增用户角色,用户权限信息
     *
     * @param username       用户名
     * @param sysUserRoleDto 用户角色输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addSysUserByInstitution(String username, SysUserRoleDto sysUserRoleDto) {
        //判断机构是否存在
        BaseResponse baseResponse = institutionFeign.getInstitutionInfoById(sysUserRoleDto.getSysId());
        Institution institution = objectMapper.convertValue(baseResponse.getData(), Institution.class);
        if (institution == null) {
            log.info("===========【机构后台新增用户角色,用户权限信息】==========【机构信息不存在!】");
            throw new BusinessException(EResultEnum.INSTITUTION_NOT_EXIST.getCode());
        }
        if (!institution.getEnabled()) {
            log.info("===========【机构后台新增用户角色,用户权限信息】==========【机构已禁用!】");
            throw new BusinessException(EResultEnum.INSTITUTION_IS_DISABLE.getCode());
        }
        SysUser dbSysUser = sysUserMapper.getSysUserByUsername(sysUserRoleDto.getUsername() + sysUserRoleDto.getSysId());
        if (dbSysUser != null) {
            log.info("=========【机构后台新增用户角色,用户权限信息】==========【用户名已存在!】");
            throw new BusinessException(EResultEnum.USER_EXIST.getCode());
        }
        //创建角色
        SysUser sysUser = createSysUser(username, sysUserRoleDto);
        sysUser.setUsername(sysUserRoleDto.getUsername() + sysUserRoleDto.getSysId());
        sysUser.setSysId(sysUserRoleDto.getSysId());
        //分配用户角色,用户权限信息
        allotSysRoleAndSysMenu(username, sysUser, sysUserRoleDto);
        return sysUserMapper.insert(sysUser);
    }

    /**
     * 机构后台修改用户角色,用户权限信息
     *
     * @param username       用户名
     * @param sysUserRoleDto 用户角色输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int updateSysUserByInstitution(String username, SysUserRoleDto sysUserRoleDto) {
        BaseResponse baseResponse = institutionFeign.getInstitutionInfoById(sysUserRoleDto.getSysId());
        Institution institution = objectMapper.convertValue(baseResponse.getData(), Institution.class);
        if (institution == null) {
            log.info("===========【机构后台修改用户角色,用户权限信息】==========【机构信息不存在!】");
            throw new BusinessException(EResultEnum.INSTITUTION_NOT_EXIST.getCode());
        }
        if (!institution.getEnabled()) {
            log.info("===========【机构后台修改用户角色,用户权限信息】==========【机构已禁用!】");
            throw new BusinessException(EResultEnum.INSTITUTION_IS_DISABLE.getCode());
        }
        SysUser dbSysUser = sysUserMapper.getSysUserByUsername(sysUserRoleDto.getUsername() + sysUserRoleDto.getSysId());
        if (dbSysUser == null) {
            log.info("=========【机构后台修改用户角色,用户权限信息】==========【用户名不存在!】");
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        //修改角色
        BeanUtils.copyProperties(sysUserRoleDto, dbSysUser);
        dbSysUser.setId(sysUserRoleDto.getUserId());
        dbSysUser.setUsername(sysUserRoleDto.getUsername() + sysUserRoleDto.getSysId());
        dbSysUser.setUpdateTime(new Date());
        dbSysUser.setModifier(username);
        //删除用户角色表中的信息
        sysUserRoleMapper.deleteByUserId(sysUserRoleDto.getUserId());
        //删除用户权限表中的信息
        sysUserMenuMapper.deleteByUserId(sysUserRoleDto.getUserId());
        //分配用户角色,用户权限信息
        allotSysRoleAndSysMenu(username, dbSysUser, sysUserRoleDto);
        return sysUserMapper.updateByPrimaryKeySelective(dbSysUser);
    }

    /**
     * 重置登录密码
     *
     * @param username 用户名
     * @param userId   用户ID
     * @return 修改条数
     */
    @Override
    @Transactional
    public int resetPassword(String username, String userId) {
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
        if (sysUser == null) {
            log.info("=========【重置登录密码】==========【用户信息不存在!】");
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        if (StringUtils.isBlank(sysUser.getEmail())) {
            log.info("=========【重置登录密码】==========【用户邮箱为空!】");
            throw new BusinessException(EResultEnum.USER_EMAIL_IS_NOT_NULL.getCode());
        }
        //随机生成六位登录密码与交易密码
        String randomPassword = IDS.randomNumber(6);
        String randomTradePassword = IDS.randomNumber(6);
        sysUser.setPassword(BCryptUtils.encode(randomPassword));
        sysUser.setTradePassword(BCryptUtils.encode(randomTradePassword));
        sysUser.setModifier(username);
        sysUser.setUpdateTime(new Date());
        //重置密码后邮件告知用户
        Map<String, Object> map = new HashMap<>();
        map.put("date", DateToolUtils.getReqDateG(new Date()));
        map.put("pwd", randomPassword);
        map.put("twd", randomTradePassword);
        messageFeign.sendTemplateMail(sysUser.getEmail(), auditorProvider.getLanguage(), Status._0, map);
        return sysUserMapper.updateByPrimaryKeySelective(sysUser);
    }

    /**
     * 修改登录密码
     *
     * @param username          用户名
     * @param updatePasswordDto 修改密码实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int updatePassword(String username, UpdatePasswordDto updatePasswordDto) {
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(updatePasswordDto.getUserId());
        if (sysUser == null) {
            log.info("=========【修改登录密码】==========【用户不存在!】");
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        //原始密码为空,直接修改
        if (StringUtils.isEmpty(updatePasswordDto.getOldPassword())) {
            sysUser.setPassword(BCryptUtils.encode(updatePasswordDto.getPassword()));
            sysUser.setModifier(username);
            sysUser.setUpdateTime(new Date());
        } else {
            //原始密码不为空,校验原始密码
            if (BCryptUtils.matches(updatePasswordDto.getOldPassword(), sysUser.getPassword())) {
                sysUser.setPassword(BCryptUtils.encode(updatePasswordDto.getPassword()));
                sysUser.setModifier(username);
                sysUser.setUpdateTime(new Date());
            } else {
                log.info("=========【修改登录密码】==========【原始密码错误!】");
                throw new BusinessException(EResultEnum.ORIGINAL_PASSWORD_ERROR.getCode());
            }
        }
        return sysUserMapper.updateByPrimaryKeySelective(sysUser);
    }

    /**
     * 修改交易密码
     *
     * @param username          用户名
     * @param updatePasswordDto 修改密码实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int updateTradePassword(String username, UpdatePasswordDto updatePasswordDto) {
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(updatePasswordDto.getUserId());
        if (sysUser == null) {
            log.info("=========【修改交易密码】==========【用户不存在!】");
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        if (BCryptUtils.matches(updatePasswordDto.getOldPassword(), sysUser.getTradePassword())) {
            sysUser.setTradePassword(BCryptUtils.encode(updatePasswordDto.getPassword()));
            sysUser.setModifier(username);
            sysUser.setUpdateTime(new Date());
        } else {
            log.info("=========【修改交易密码】==========【原始密码错误!】");
            throw new BusinessException(EResultEnum.ORIGINAL_PASSWORD_ERROR.getCode());
        }
        return sysUserMapper.updateByPrimaryKeySelective(sysUser);
    }

    /**
     * 根据用户名查询用户关联角色,权限信息
     *
     * @param userName 用户名
     * @return SysUserVO
     */
    @Override
    public SysUserVO getSysUser(String userName) {
        return sysUserMapper.getSysUser(userName);
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
        sysUserDto.setSort("s.create_time");
        if (AsianWalletConstant.OPERATION.equals(sysUserDto.getPermissionType())) {
            //运营系统
            sysUserList = sysUserMapper.pageGetSysUserByOperation(sysUserDto);
        } else {
            sysUserDto.setUsername(sysUserDto.getUsername() + sysUserDto.getSysId());
            sysUserList = sysUserMapper.pageGetSysUserByOperation(sysUserDto);
            for (SysUserSecVO sysUserSecVO : sysUserList) {
                if (!StringUtils.isEmpty(sysUserSecVO.getSysId())) {
                    sysUserSecVO.setUsername(sysUserSecVO.getUsername().replace(sysUserSecVO.getSysId(), ""));
                    sysUserSecVO.setCreator(sysUserSecVO.getUsername().replace(sysUserSecVO.getSysId(), ""));
                    sysUserSecVO.setModifier(sysUserSecVO.getUsername().replace(sysUserSecVO.getSysId(), ""));
                }
            }
        }
        return new PageInfo<>(sysUserList);
    }

    /**
     * 查询用户详情
     *
     * @param username 用户名
     * @return 用户详情实体
     */
    @Override
    public SysUserDetailVO getSysUserDetail(String username) {
        SysUserVO sysUser = sysUserMapper.getSysUser(username);
        SysUserDetailVO sysUserDetailVO = new SysUserDetailVO();
        BeanUtils.copyProperties(sysUser, sysUserDetailVO);
        List<ResRole> roleList = Lists.newArrayList();
        Set<ResPermissions> permissionList = Sets.newHashSet();
        for (SysRoleVO sysRoleVO : sysUser.getRole()) {
            ResRole resRole = new ResRole();
            if (StringUtils.isNotBlank(sysRoleVO.getRoleName())) {
                BeanUtils.copyProperties(sysRoleVO, resRole);
                roleList.add(resRole);
            }
            for (SysMenuVO sysMenuVO : sysRoleVO.getMenus()) {
                ResPermissions resPermissions = new ResPermissions();
                BeanUtils.copyProperties(sysMenuVO, resPermissions);
                permissionList.add(resPermissions);
            }
        }
        sysUserDetailVO.setRoleList(roleList);
        sysUserDetailVO.setPermissionList(permissionList);
        return sysUserDetailVO;
    }

    /**
     * 开户发送邮件
     *
     * @param institutionDTO 机构实体
     * @return 用户详情实体
     */
    @Override
    public void openAccountEmail(InstitutionDTO institutionDTO) {
        log.info("=========【开户发送邮件】==========【START】");
        try {
            if (!StringUtils.isEmpty(institutionDTO.getInstitutionEmail())) {
                log.info("=========【开户发送邮件】==========【发送的机构邮箱】 institutionEmail: {}", institutionDTO.getInstitutionEmail());
                Map<String, Object> map = new HashMap<>();
                SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd");
                //发送日期
                map.put("dateTime", sf.format(new Date()));
                //机构名称
                map.put("institutionName", institutionDTO.getCnName());
                //机构code
                map.put("institutionCode", institutionDTO.getInstitutionId());
                messageFeign.sendTemplateMail(institutionDTO.getInstitutionEmail(), institutionDTO.getLanguage(), Status._3, map);
            }
        } catch (Exception e) {
            log.error("=========【开户发送邮件】==========【开户发送邮件失败】 institutionEmail: {} | errorMessage: {}", institutionDTO.getInstitutionEmail(), e.getMessage());
        }
        log.info("=========【开户发送邮件】==========【END】");
    }
}
