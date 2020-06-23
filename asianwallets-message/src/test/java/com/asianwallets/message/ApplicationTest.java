package com.asianwallets.message;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.utils.IDS;
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
        smsService.sendSimpleTemplate("zh-cn", Status._0, "18270654875", map);
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
        map.put("ticketQrCode", "http://192.168.124.27:8080/imagesaas/2020-06-22/38919e1a-eab8-46f0-a5e1-cab9fa16ff29.png");
        map.put("ticketId", "123456789");
        map.put("startTime", "2020-11-11 00:00:00");
        map.put("endTime", "2020-11-15 23:59:59");
        map.put("unusableTime", "2020-11-12,2020-11-13");
        map.put("ruleDescription", "每人只能使用一张");
        map.put("shopAddresses", "上海金科路店");
        emailService.sendTemplateMail("842505302@qq.com","zh-cn",Status._4,map);
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
}

