package com.asianwallets.trade.service.impl;

import com.asianwallets.trade.service.CommonService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 共通模块的实现类
 */
@Service
public class CommonServiceImpl implements CommonService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     *校验密码
     * @param oldPassword
     * @param password
     * @return
     */
    @Override
    public Boolean checkPassword(String oldPassword, String password) {
        return passwordEncoder.matches(oldPassword, password);
    }
}
