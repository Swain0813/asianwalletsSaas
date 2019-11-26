package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysUser;
import com.asianwallets.common.vo.SysUserVO;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名,权限类型查询用户关联角色,权限信息
     *
     * @param userName       用户名
     * @param permissionType 权限类型
     * @return SysUserVO
     */
    SysUserVO getSysUser(@Param("userName") String userName, @Param("sysId") String sysId, @Param("permissionType") Integer permissionType);
}