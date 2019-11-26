package com.asianwallets.message.service;

import com.asianwallets.common.enums.Status;

import java.util.Map;

/**
 * 邮件相关业务模块
 * Created by yangshanlong@asianwallets.com on 2019/01/23.
 */
public interface EmailService {

    /**
     *发送简单邮件
     * @param sendTo 收件人地址
     * @param title 邮件标题
     * @param content 邮件内容
     * @return 邮件发送返回结果
     */
    boolean sendSimpleMail(String sendTo, String title, String content);

    /**
     *发送模板邮件
     * @param sendTo 收件人地址
     * @param languageNum 语言
     * @param templateNum 模板号
     * @param param 邮件模板中的变量
     * @return
     */
    boolean sendTemplateMail(String sendTo,
                             String languageNum,
                             Status templateNum,
                             Map<String, Object> param);
}
