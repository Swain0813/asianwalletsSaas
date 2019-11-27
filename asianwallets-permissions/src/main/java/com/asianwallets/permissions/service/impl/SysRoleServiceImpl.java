package com.asianwallets.permissions.service.impl;

import com.asianwallets.common.entity.SysRole;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.dao.SysRoleMapper;
import com.asianwallets.permissions.dao.SysRoleMenuMapper;
import com.asianwallets.permissions.dto.SysRoleDto;
import com.asianwallets.permissions.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

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

}
