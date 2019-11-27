package com.asianwallets.permissions.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BCrypt加密工具类
 */
public class BCryptUtils {

    /**
     * 加密字符串
     *
     * @param str 字符串
     * @return 密文
     */
    public static String encode(String str) {
        return BCrypt.hashpw(str, BCrypt.gensalt());
    }

    /**
     * 比较字符串值是否一致
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return true or false
     */
    public static boolean matches(String str1, String str2) {
        return new BCryptPasswordEncoder().matches(str1, str2);
    }
}
