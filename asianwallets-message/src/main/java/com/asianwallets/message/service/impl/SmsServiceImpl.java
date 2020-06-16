package com.asianwallets.message.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.utils.HttpSend;
import com.asianwallets.message.service.SmsService;
import com.asianwallets.message.utils.HttpUtil;
import com.google.common.collect.Lists;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import freemarker.template.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 短信模块的实现类
 * Created by yangshanlong@asianwallets.com on 2019/01/22.
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    /**
     * 国际短信用
     */
    @Value("${sms.send.api.inter.account}")
    private String accountInter;//账号

    @Value("${sms.send.api.inter.pwd}")
    private String passwordInter;//密码

    @Value("${sms.send.api.inter.intapi}")
    private String urlInt;//调用url

    /**
     * 普通短信用
     */
    boolean needstatus = true;// 是否需要状态报告，需要true，不需要false

    String extno = null;// 扩展码

    @Value("${sms.send.api.simple.account}")
    private String account;

    @Value("${sms.send.api.simple.pwd}")
    private String pwd;

    @Value("${sms.send.api.simple.batchSendSM}")
    private String url;

    @Autowired
    private Configuration configuration; //freeMarker configuration


    /**
     * 普通发送
     * @param mobile
     * @param content
     * @return
     */
    @Override
    public boolean sendSimple(String mobile,String content) {
        String returnCode = null;
        try {
            //获取多个手机号
            List<String> lists = this.getSendMobiles(mobile);
            for(String list:lists){
                String returnString = HttpSend.batchSend(url, account, pwd, list, content, needstatus, extno);
                if (returnString != null) {
                    returnCode = returnString.split(",")[1].split("\n")[0];
                    if (returnCode.equals("0")) {
                        //return true;
                        log.info("普通短信发送成功的场合returnCode{}",returnCode);
                    }
                }
            }
        } catch (Exception e) {
            log.error("普通短信发送失败{}", mobile);
        }
        return false;
    }

    /**
     * 国际短信发送
     * @param mobile 手机号
     * @param content 内容
     * @return
     */
    @Override
    public boolean sendInternation(String mobile,String content){
        //获取多个手机号
        List<String> lists = this.getSendMobiles(mobile);
        for (String list : lists) {
            //组装请求参数
            JSONObject map = new JSONObject();
            map.put("account", accountInter);
            map.put("password", passwordInter);
            map.put("msg", content);
            map.put("mobile", list);
            String params = map.toString();
            String code = null;
            try {
                String result = HttpUtil.post(urlInt, params);
                JSONObject jsonObject = JSON.parseObject(result);
                code = jsonObject.get("code").toString();
                if (code.equals("0")) {
                    //return true;//成功的场合
                    log.info("国际短信发送成功的场合的code{}",code);
                }
            } catch (Exception e) {
                log.error("国际短信发送失败{}", mobile);
            }
        }
        return false;
    }

    /**
     * 国内普通短信模板
     * @param language 语言
     * @param num 模板号
     * @param mobile 手机号
     * @param content 模板里的参数
     * @return
     */
    @Override
    public boolean sendSimpleTemplate(String language, Status num, String mobile, Map<String, Object> content){
        Template t = null;
        try {
            t = configuration.getTemplate(language + "/sms/" + num + ".ftl");
            if (t == null) {
                t = configuration.getTemplate("zh-cn/sms/" + num + ".ftl");
            }
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, content);
            return sendSimple(mobile, html);
        } catch (Exception e) {
            log.error("国内普通短信模板发送失败{}", mobile);
        }
        return false;
    }

    /**
     * 国际短信模板
     * @param language 语言
     * @param num 模板号
     * @param mobile 手机号
     * @param content 模板里的参数
     * @return
     */
    @Override
    public boolean sendIntTemplate(String language,Status num,String mobile,Map<String, Object> content){
        Template t = null;
        try {
            t = configuration.getTemplate(language + "/sms/" + num + ".ftl");
            if (t == null) {
                t = configuration.getTemplate("zh-cn/sms/" + num + ".ftl");
            }
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, content);
            return sendInternation(mobile, html);
        } catch (Exception e) {
            log.error("国际短信模板发送失败{}", mobile);
        }
        return false;
    }


    /**
     * 获取多个手机号
     * @param mobiles
     * @return
     */
    private List<String> getSendMobiles(String mobiles){
        //支持多个手机号
        List<String> list= Lists.newArrayList();
        if(mobiles.contains(",")) {
            String[] ids = mobiles.split(",");//获取多个手机号
            list= Arrays.asList(ids);
        }else{
            list.add(mobiles);
        }
        return list;
    }
}
