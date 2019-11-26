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
        map.put("draweeName", "龙哥");
        map.put("channelCallbackTime", "111111");
        map.put("reqIp", "111111");
        map.put("institutionOrderId", "111111");
        map.put("institutionName", "111111");
        map.put("orderCurrency", "111111");
        map.put("amount", "111111");
        map.put("goodsDescription", "111111");
        map.put("issuerId", "111111");
        map.put("invoiceNo", "111111");
        map.put("referenceNo", "111111");
        emailService.sendTemplateMail("842505302@qq.com","zh-cn",Status._1,map);
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

