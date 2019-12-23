package com.asianwallets.trade.service;

/**
 * 共通模块
 */
public interface CommonService {

    /**
     *校验密码
     * @param oldPassword
     * @param password
     * @return
     */
    Boolean checkPassword(String oldPassword, String password);
}
