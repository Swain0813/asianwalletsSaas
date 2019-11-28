package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysUserRole;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    @Select("select id from sys_role where role_name like '%机构管理员%'")
    String getInstitutionRoleId();

    @Select("select id from sys_role where role_name like '%POS机管理员%'")
    String getPOSRoleId();

    @Select("select id from sys_role where role_name like '%代理管理员%'")
    String getAgencyRoleId();

    @Select("select id from sys_role where role_name like '%商户管理员%'")
    String getMerchantRoleId();

    @Select("select id from sys_role where role_name like '%集团商户管理员%'")
    String getGroupRoleId();
}
