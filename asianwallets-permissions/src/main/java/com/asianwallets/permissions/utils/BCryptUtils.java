package com.asianwallets.permissions.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * BCrypt加密工具类
 */
public class BCryptUtils {

    /**
     * 加密字符串
     *
     * @return 密文
     */
    public String encode(String str) {
        return BCrypt.hashpw(str, BCrypt.gensalt());
    }
}
