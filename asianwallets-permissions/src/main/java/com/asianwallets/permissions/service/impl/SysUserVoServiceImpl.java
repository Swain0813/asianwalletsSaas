package com.asianwallets.permissions.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.SysMenuVO;
import com.asianwallets.common.vo.SysRoleVO;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.dao.SysUserMapper;
import com.asianwallets.permissions.service.SysUserVoService;
import com.asianwallets.permissions.utils.SpringSecurityUser;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-01-11 10:42
 **/
@Service
@Slf4j
@Transactional
public class SysUserVoServiceImpl implements SysUserVoService {
    @Autowired
    private SysUserMapper sysUserMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SysUserVO getSysUser(String userName, String sysId, Integer permissionType){
        return sysUserMapper.getSysUser(userName, sysId, permissionType);
    }

    @Override
    public UserDetails loadUserByUsername(String json) throws UsernameNotFoundException {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String username = jsonObject.getString("username");
        String sysId = jsonObject.getString("sysId");
        Integer permissionType = jsonObject.getInteger("permissionType");
        SysUserVO user = getSysUser(username, sysId, permissionType);
        if (user == null) {
            throw new BusinessException(EResultEnum.USER_NOT_EXIST.getCode());
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<SysRoleVO> listrole = user.getRole();
        Set<String> set = Sets.newHashSet();
        for (SysRoleVO sysRoleVO : listrole) {
            if (sysRoleVO != null) {
                for (SysMenuVO permission : sysRoleVO.getMenus()) {
                    set.add(permission.getId());
                }
            }
        }
        for (String s : set) {
            authorities.add(new SimpleGrantedAuthority(s));
        }
        SpringSecurityUser userDetails = new SpringSecurityUser();
        userDetails.setSysUser(user);
        userDetails.setAuthorities(authorities);
        return userDetails;
    }



}
