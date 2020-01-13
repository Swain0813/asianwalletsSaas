package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 根据用户id修改角色id
     *
     * @param userId 用户ID
     * @return 修改条数
     */
    int updateRoleIdByUserId(@Param("userId") String userId, @Param("roleId") String roleId);

    /**
     * 根据用户id查询角色id
     *
     * @param userId 用户ID
     * @return 角色ID
     */
    String selectRoleIdByUserId(String userId);
}