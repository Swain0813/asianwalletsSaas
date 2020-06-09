package com.asianwallets.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

public final class AESUtil {

    public static final String password = "YrF/TUKJqzBDCJ5ubyv00Q==";

    /**
     * 解密
     *
     * @param content
     * @return
     */
    public static String aesDecrypt(String content) {
        byte[] key = Base64.decode(password);
        //构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        //解密为字符串
        return aes.decryptStr(content, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 加密
     *
     * @param content
     * @return
     */
    public static String aesEncrypt(String content) {
        byte[] key = Base64.decode(password);
        //构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        //加密为16进制表示
        return aes.encryptHex(content);
    }

}