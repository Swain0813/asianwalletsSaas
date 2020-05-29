package com.asianwallets.common.dto.upi;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密
 */
public class AESUtils {

    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "AES";

    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHM_STR = "AES/ECB/PKCS5Padding";

    /**
     * SecretKeySpec类是KeySpec接口的实现类,用于构建秘密密钥规范
     */
    private SecretKeySpec key;

    public AESUtils(String hexKey) {
        key = new SecretKeySpec(hexKey.getBytes(), ALGORITHM);
    }

    /**
     * AES加密
     * @param data
     * @return
     * @throws Exception
     */
    public String encryptData(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_STR); // 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
        return new BASE64Encoder().encode(cipher.doFinal(data.getBytes()));
    }

    /**
     * AES解密
     * @param base64Data
     * @return
     * @throws Exception
     */
    public String decryptData(String base64Data) throws Exception{
        Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(base64Data)));
    }

    /**
     * 测试用
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        AESUtils util = new AESUtils("abcdefghijklmnop"); // 密钥
        System.out.println("cardNo:"+util.encryptData("1234")); // 加密
        System.out.println("exp:"+util.decryptData("34+Jzs4KkwaCQWVyyAgwLA==")); // 解密
    }
}