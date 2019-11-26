package com.asianwallets.message.service;

import com.asianwallets.common.enums.Status;

import java.util.Map;

/**
 * 短信相关业务模块
 * Created by yangshanlong@asianwallets.com on 2019/01/22.
 */
public interface SmsService {

    /**
     *国内普通发送
     * @param mobile
     * @param content
     * @return
     */
    boolean sendSimple(String mobile, String content);


    /**
     * 国际短信发送
     * @param mobile 手机号 需要有国家代码比如中国是手机号前+86
     * @param content 内容
     * @return
     */
    boolean sendInternation(String mobile, String content);

    /**
     *国内普通短信模板
     * @param language 语言
     * @param num 模板号
     * @param mobile 手机号
     * @param content 模板里的参数
     * @return
     */
    boolean sendSimpleTemplate(String language, Status num, String mobile, Map<String, Object> content);

    /**
     * 国际短信模板
     * @param language 语言
     * @param num 模板号
     * @param mobile 手机号
     * @param content 模板里的参数
     * @return
     */
    boolean sendIntTemplate(String language, Status num, String mobile, Map<String, Object> content);



}
