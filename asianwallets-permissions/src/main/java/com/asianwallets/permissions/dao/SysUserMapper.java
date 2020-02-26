package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SysUser;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.dto.SysUserDto;
import com.asianwallets.permissions.vo.SysUserSecVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户关联角色,权限信息
     *
     * @param userName 用户名
     * @return SysUserVO
     */
    SysUserVO getSysUser(String userName);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return SysUser
     */
    SysUser getSysUserByUsername(String username);

    /**
     * 运营系统查询用户信息
     *
     * @param sysUserDto 用户查询实体
     * @return 用户集合
     */
    List<SysUserSecVO> pageGetSysUserByOperation(SysUserDto sysUserDto);

    /**
     * 根据系统ID查询用户ID
     *
     * @param sysId 系统ID
     * @return 用户ID集合
     */
    List<String> selectUserIdBySysId(String sysId);


    /**
     * 根据系统id查询用户信息
     * @param sysId
     * @return
     */
    SysUser getSysUserBySysId(String sysId);
}
