package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysRole;
import com.asianwallets.permissions.dto.SysRoleSecDto;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色名,系统ID查询角色
     *
     * @param roleName 角色名
     * @param sysId    系统ID
     * @return 用户实体
     */
    SysRole getSysRoleByNameAndSysId(String roleName, String sysId);

    /**
     * 查询角色信息
     *
     * @param sysRoleSecDto 角色查询实体
     * @return 角色集合
     */
    List<SysRole> pageGetSysRole(SysRoleSecDto sysRoleSecDto);
}