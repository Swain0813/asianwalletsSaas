package com.asianwallets.permissions.service.impl;

import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.SysMenu;
import com.asianwallets.common.entity.SysRole;
import com.asianwallets.common.entity.SysRoleMenu;
import com.asianwallets.common.entity.SysUser;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.permissions.dao.*;
import com.asianwallets.permissions.dto.*;
import com.asianwallets.permissions.service.SysMenuService;
import com.asianwallets.permissions.vo.FirstMenuVO;
import com.asianwallets.permissions.vo.SecondMenuVO;
import com.asianwallets.permissions.vo.ThreeMenuVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysUserMenuMapper sysUserMenuMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 添加一二三级菜单权限信息
     *
     * @param username     用户名
     * @param firstMenuDto 一级权限输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addThreeLayerMenu(String username, FirstMenuDto firstMenuDto) {
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
        if (!ArrayUtil.isEmpty(firstMenuDto.getSecondMenuDtoList())) {
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
                if (!ArrayUtil.isEmpty(secondMenuDto.getThreeMenuDtoList())) {
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
            }
        }
        return sysMenuMapper.insertList(menuList);
    }

    /**
     * 添加二三级菜单权限信息
     *
     * @param username      用户名
     * @param secondMenuDto 二级权限输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addTwoLayerMenu(String username, SecondMenuDto secondMenuDto) {
        List<SysMenu> menuList = new ArrayList<>();
        SysMenu secondMenu = new SysMenu();
        secondMenu.setId(IDS.uuid2());
        secondMenu.setLevel(1);
        secondMenu.setParentId(secondMenuDto.getParentId());
        secondMenu.setEnName(secondMenuDto.getEName());
        secondMenu.setCnName(secondMenuDto.getCName());
        secondMenu.setPermissionType(secondMenuDto.getPermissionType());
        secondMenu.setDescription(secondMenuDto.getDescription());
        secondMenu.setSort(0);
        secondMenu.setCreateTime(new Date());
        secondMenu.setCreator(username);
        secondMenu.setEnabled(true);
        menuList.add(secondMenu);
        if (!ArrayUtil.isEmpty(secondMenuDto.getThreeMenuDtoList())) {
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
     * 添加三级菜单权限信息
     *
     * @param username     用户名
     * @param threeMenuDto 三级权限输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addOneLayerMenu(String username, ThreeMenuDto threeMenuDto) {
        SysMenu threeMenu = new SysMenu();
        threeMenu.setId(IDS.uuid2());
        threeMenu.setLevel(2);
        threeMenu.setParentId(threeMenuDto.getParentId());
        threeMenu.setEnName(threeMenuDto.getEName());
        threeMenu.setCnName(threeMenuDto.getCName());
        threeMenu.setPermissionType(threeMenuDto.getPermissionType());
        threeMenu.setDescription(threeMenuDto.getDescription());
        threeMenu.setSort(0);
        threeMenu.setCreateTime(new Date());
        threeMenu.setCreator(username);
        threeMenu.setEnabled(true);
        return sysMenuMapper.insert(threeMenu);
    }

    /**
     * 添加菜单权限信息
     *
     * @param username   用户名
     * @param sysMenuDto 权限输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addMenu(String username, SysMenuDto sysMenuDto) {
        SysMenu sysMenu = new SysMenu();
        sysMenu.setId(IDS.uuid2());
        sysMenu.setEnName(sysMenuDto.getEnName());
        sysMenu.setCnName(sysMenuDto.getCnName());
        sysMenu.setParentId(sysMenuDto.getParentId());
        sysMenu.setPermissionType(sysMenuDto.getPermissionType());
        sysMenu.setDescription(sysMenuDto.getDescription());
        sysMenu.setLevel(sysMenuDto.getLevel());
        sysMenu.setCreateTime(new Date());
        sysMenu.setCreator(username);
        sysMenu.setSort(0);
        sysMenu.setEnabled(true);
        return sysMenuMapper.insert(sysMenu);
    }

    /**
     * 删除权限信息
     *
     * @param menuId 权限ID
     * @return 修改条数
     */
    @Override
    @Transactional
    public int deleteMenu(String menuId) {
        SysMenu sysMenu = sysMenuMapper.selectByPrimaryKey(menuId);
        if (sysMenu == null) {
            log.info("=========【删除权限信息】==========【权限信息不存在!】");
            throw new BusinessException(EResultEnum.MENU_NOT_EXIST.getCode());
        }
        switch (sysMenu.getLevel()) {
            case AsianWalletConstant.ZERO:
                //查询关联权限ID
                FirstMenuVO firstMenuVO = sysMenuMapper.selectAllMenuById(menuId);
                if (firstMenuVO == null) {
                    log.info("=========【删除权限信息】==========【数据异常!】");
                    throw new BusinessException(EResultEnum.REQUEST_REMOTE_ERROR.getCode());
                }
                List<String> menuIds = new ArrayList<>();
                menuIds.add(firstMenuVO.getId());
                for (SecondMenuVO secondMenuVO : firstMenuVO.getSecondMenuVOS()) {
                    menuIds.add(secondMenuVO.getId());
                    for (ThreeMenuVO threeMenuVO : secondMenuVO.getThreeMenuVOS()) {
                        menuIds.add(threeMenuVO.getId());
                    }
                }
                //删除中间表的关联关系
                sysUserMenuMapper.deleteByMenuIdList(menuIds);
                sysRoleMenuMapper.deleteByMenuIdList(menuIds);
                //删除一级权限关联的所有权限
                return sysMenuMapper.deleteByMenuIdList(menuIds);
            case AsianWalletConstant.ONE:
                //查询所有关联权限ID
                List<String> menuIdList = sysMenuMapper.selectMenuByParentId(menuId);
                menuIdList.add(menuId);
                //删除中间表的关联关系
                sysUserMenuMapper.deleteByMenuIdList(menuIdList);
                sysRoleMenuMapper.deleteByMenuIdList(menuIdList);
                //删除二级权限关联的所有权限
                return sysMenuMapper.deleteByMenuIdList(menuIdList);
            case AsianWalletConstant.TWO:
                //删除中间表的关联关系
                sysUserMenuMapper.deleteByMenuId(menuId);
                sysRoleMenuMapper.deleteByMenuId(menuId);
                //删除三级权限自身
                return sysMenuMapper.deleteByPrimaryKey(menuId);
            default:
                log.info("=========【删除权限信息】==========【层级信息不存在!】 Level: {}", sysMenu.getLevel());
                throw new BusinessException(EResultEnum.REQUEST_REMOTE_ERROR.getCode());
        }
    }

    /**
     * 修改权限信息
     *
     * @param username   用户名
     * @param sysMenuDto 权限输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int updateMenu(String username, SysMenuDto sysMenuDto) {
        SysMenu sysMenu = sysMenuMapper.selectByPrimaryKey(sysMenuDto.getMenuId());
        if (sysMenu == null) {
            log.info("=========【修改权限信息】==========【权限信息不存在!】");
            throw new BusinessException(EResultEnum.MENU_NOT_EXIST.getCode());
        }
        sysMenu.setCnName(sysMenuDto.getCnName());
        sysMenu.setEnName(sysMenuDto.getEnName());
        sysMenu.setEnabled(sysMenuDto.getEnabled());
        sysMenu.setModifier(username);
        sysMenu.setUpdateTime(new Date());
        return sysMenuMapper.updateByPrimaryKeySelective(sysMenu);
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
        List<FirstMenuVO> menuList = sysMenuMapper.selectAllMenuByPermissionType(permissionType);
        if (StringUtils.isNotBlank(userId)) {
            Set<String> menuSet = sysMenuMapper.selectMenuByUserId(userId);
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
        List<FirstMenuVO> menuList = sysMenuMapper.selectAllMenuByPermissionType(permissionType);
        if (StringUtils.isNotBlank(roleId)) {
            Set<String> menuSet = sysMenuMapper.selectMenuByRoleId(roleId);
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
     * 运营后台修改机构权限
     *
     * @param username               用户名
     * @param updateInsPermissionDto 运营后台修改机构权限dto
     * @return 修改条数
     */
    @Override
    @Transactional
    public int updateInsPermission(String username, UpdateInsPermissionDto updateInsPermissionDto) {
        //查询机构管理员
        SysUser sysUser = sysUserMapper.getSysUserByUsername("admin" + updateInsPermissionDto.getInstitutionId());
        if (sysUser == null) {
            throw new BusinessException(EResultEnum.REQUEST_REMOTE_ERROR.getCode());
        }
        //查询机构默认角色
        SysRole sysRole = sysRoleMapper.selectBySysIdAndRoleCode(updateInsPermissionDto.getInstitutionId());
        if (sysRole == null) {
            //创建机构定制管理员角色
            sysRole = new SysRole();
            String roleId = IDS.uuid2();
            sysRole.setId(roleId);
            sysRole.setSysId(updateInsPermissionDto.getInstitutionId());
            sysRole.setPermissionType(AsianWalletConstant.INSTITUTION);
            sysRole.setRoleName("机构定制管理员");
            sysRole.setRoleCode("INSTITUTION_ADMIN");
            sysRole.setCreateTime(new Date());
            sysRole.setCreator(username);
            sysRole.setEnabled(true);
            sysRoleMapper.insert(sysRole);
            //修改机构管理员对应角色
            sysUserRoleMapper.updateRoleIdByUserId(sysUser.getId(), roleId);
            //启用的权限集合
            List<String> openIdList = updateInsPermissionDto.getOpenIdList();
            //分配角色对应权限
            List<SysRoleMenu> sysRoleMenuList = new ArrayList<>();
            for (String openId : openIdList) {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setId(IDS.uuid2());
                sysRoleMenu.setRoleId(sysRole.getId());
                sysRoleMenu.setMenuId(openId);
                sysRoleMenu.setEnabled(true);
                sysRoleMenu.setCreateTime(new Date());
                sysRoleMenu.setCreator(username);
            }
            sysRoleMenuMapper.insertList(sysRoleMenuList);
        }
        //禁用的权限集合
        List<String> offIdList = updateInsPermissionDto.getOffIdList();
        //机构对应所有用户
        List<String> userIdList = sysUserMapper.selectUserIdBySysId(updateInsPermissionDto.getInstitutionId());
        //机构对应所有角色
        List<String> roleIdList = sysRoleMapper.selectRoleIdBySysId(updateInsPermissionDto.getInstitutionId());
        //禁用所有用户权限
        for (String insUserId : userIdList) {
            for (String offId : offIdList) {
                sysUserMenuMapper.updateEnabledByUserIdAndMenuId(insUserId, offId, false);
            }
        }
        //禁用所有角色权限
        for (String insRoleId : roleIdList) {
            for (String offId : offIdList) {
                sysRoleMenuMapper.updateEnabledByRoleIdAndMenuId(insRoleId, offId, false);
            }
        }
//        //启用机构管理员权限
//        for (String openId : openIdList) {
//            sysUserMenuMapper.updateEnabledByUserIdAndMenuId(sysUser.getId(), openId, true);
//        }
        return 1;
    }

    /**
     * 运营后台查询机构权限
     *
     * @param updateInsPermissionDto 运营后台机构权限dto
     * @return
     */
    @Override
    public List<FirstMenuVO> getInsPermission(UpdateInsPermissionDto updateInsPermissionDto) {
//        //查询机构管理员
//        SysUser sysUser = sysUserMapper.getSysUserByUsername("admin" + updateInsPermissionDto.getInstitutionId());
//        if (sysUser == null) {
//            throw new BusinessException(EResultEnum.REQUEST_REMOTE_ERROR.getCode());
//        }
        SysRole sysRole = sysRoleMapper.selectBySysIdAndRoleCode(updateInsPermissionDto.getInstitutionId());
        if (sysRole == null) {
            sysRole = sysRoleMapper.getInstitutionRoleId();
            if (sysRole == null) {
                throw new BusinessException(EResultEnum.REQUEST_REMOTE_ERROR.getCode());
            }
        }
        //根据权限类型查询所有权限
        List<FirstMenuVO> menuList = sysMenuMapper.selectAllMenuByPermissionType(updateInsPermissionDto.getPermissionType());
        Set<String> menuSet = sysMenuMapper.selectMenuByRoleId(sysRole.getId());
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
        return menuList;
    }
}
