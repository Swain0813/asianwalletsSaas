package com.asianwallets.message;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.MD5Util;
import com.asianwallets.message.service.EmailService;
import com.asianwallets.message.service.SmsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private SmsService smsService;

    @Autowired
    private EmailService emailService;

    @Test
    public void contextLoads() {
    }

    /**
     * 普通发送-ok
     */
    @Test
    public void sendSimple(){
        smsService.sendSimple("18800330943,18270654875","hello world！！！");
    }

    /**
     * 国际短信发送--ok
     */
    @Test
    public void sendInternation(){
        smsService.sendInternation("8618800330943,8618270654875","【AsianWallets】hello world！！！");
    }

    /**
     * 国内普通短信模板
     */
    @Test
    public void sendSimpleTemplate(){
        String code = IDS.getStringRandom(6);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("verificationcode", code);
        smsService.sendSimpleTemplate("zh-cn", Status._0, "18800330943", map);
    }

    /**
     * 国际短信模板
     */
    @Test
    public void sendIntTemplate(){
        String code = IDS.getStringRandom(6);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("verificationcode", code);
        smsService.sendIntTemplate("zh-cn", Status._0, "8618270654875,8618800330943", map);
    }

    /**
     * 发送简单邮件
     */
    @Test
    public void sendSimpleMail(){
        emailService.sendSimpleMail("842505302@qq.com,965508875@qq.com","我是帅哥","我是帅哥");
    }

    /**
     * 发送模板邮件
     */
    @Test
    public void sendTemplateMail(){
        String code = IDS.getStringRandom(6);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("merchantName", "龙哥商户");
        map.put("activityTheme", "双十一活动");
        map.put("content", "八折活动,上不封顶");
        map.put("ticketQrCode", gererateShortUrl("http://192.168.124.27:8080/imagesaas/2020-06-22/38919e1a-eab8-46f0-a5e1-cab9fa16ff29.png"));
        map.put("ticketId", "1042598912928546816");
        map.put("startTime", "2020-11-11 00:00:00");
        map.put("endTime", "2020-11-15 23:59:59");
        map.put("unusableTime", "2020-11-12,2020-11-13");
        map.put("ruleDescription", "每人只能使用一张");
        map.put("shopAddresses", "上海金科路店");
        emailService.sendTemplateMail("842505302@qq.com","zh-cn",Status._4,map);
    }


    @Test
    public void sendTemplateMsg(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("merchantName", "龙哥商户");
        map.put("activityTheme", "双十一活动");
        map.put("content", "八折活动,上不封顶");
        map.put("ticketQrCode", gererateShortUrl("http://192.168.124.27:8080/imagesaas/2020-06-22/38919e1a-eab8-46f0-a5e1-cab9fa16ff29.png"));
        map.put("ticketId", "1042598912928546816");
        map.put("startTime", "2020-11-11 00:00:00");
        map.put("endTime", "2020-11-15 23:59:59");
        map.put("unusableTime", "2020-11-12,2020-11-13");
        map.put("ruleDescription", "每人只能使用一张");
        map.put("shopAddresses", "上海金科路店");
        smsService.sendIntTemplate("zh-cn", Status._4, "8618800330943", map);
    }

    /**
     * 开户邮件的测试
     */
    @Test
    public void sendInstitution(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dateTime", "龙哥");
        map.put("institutionName", "111111");
        map.put("institutionCode", "111111");
        emailService.sendTemplateMail("842505302@qq.com,965508875@qq.com","en-us",Status._3,map);
    }

    /**
     * 将长链接转换为短链接
     * @param url
     * @return
     */
    private String gererateShortUrl(String url) {
        // 可以自定义生成 MD5 加密字符传前的混合 KEY
        String key = "caron" ;
        // 要使用生成 URL 的字符
        String[] chars = new String[] { "a" , "b" , "c" , "d" , "e" , "f" , "g" , "h" ,
                "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" ,
                "u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" ,
                "6" , "7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" ,
                "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" ,
                "U" , "V" , "W" , "X" , "Y" , "Z"

        };
        // 对传入网址进行 MD5 加密
        String sMD5EncryptResult = MD5Util.getMD5String(key+url);
        String hex = sMD5EncryptResult;
        // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
        String sTempSubString = hex.substring(2 * 8, 2 * 8 + 8);    //固定取第三组

        // 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
        long lHexLong = 0x3FFFFFFF & Long.parseLong (sTempSubString, 16);
        String outChars = "" ;
        for ( int j = 0; j < 6; j++) {
            // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
            long index = 0x0000003D & lHexLong;
            // 把取得的字符相加
            outChars += chars[( int ) index];
            // 每次循环按位右移 5 位
            lHexLong = lHexLong >> 5;
        }
        return outChars;
    }
}

