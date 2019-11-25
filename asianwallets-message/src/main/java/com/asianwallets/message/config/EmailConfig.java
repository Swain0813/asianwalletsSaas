package com.asianwallets.message.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class EmailConfig {
    /**
     * 发件邮箱
     */
    @Value("${msg.send.api.emailFrom}")
    private   String emailFrom;

    /**
     * 账号
     */
    @Value("${msg.send.api.simple.account}")
    private   String  apiUser;

    /**
     * apiKey
     */
    @Value("${msg.send.api.simple.key}")
    private  String apiKey;

    /**
     *fromName
     */
    @Value("${msg.send.api.simple.name}")
    private String fromName;

}
