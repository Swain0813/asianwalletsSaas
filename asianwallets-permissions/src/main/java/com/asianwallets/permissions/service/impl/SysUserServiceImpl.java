package com.asianwallets.permissions.service.impl;

import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.dao.SysUserMapper;
import com.asianwallets.permissions.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 用户业务接口实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 根据用户名查询用户关联角色,权限信息
     *
     * @param userName 用户名
     * @return SysUserVO
     */
    public SysUserVO getSysUser(String userName) {
        return sysUserMapper.getSysUser(userName);
    }
}
