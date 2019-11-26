package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysUserRole;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户id删除用户角色中间表信息
     *
     * @param userId 用户ID
     * @return 删除条数
     */
    int deleteByUserId(String userId);

}