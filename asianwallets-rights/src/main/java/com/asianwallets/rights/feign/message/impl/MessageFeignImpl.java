package com.asianwallets.rights.feign.message.impl;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.rights.feign.message.MessageFeign;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *短信和邮件的服务的实现类
 */
@Component
public class MessageFeignImpl implements MessageFeign {

    /**
     * 国内普通发送
     * @param mobile
     * @param content
     * @return
     */
    @Override
    public BaseResponse sendSimple(String mobile, String content) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 国际短信发送
     * @param mobile
     * @param content
     * @return
     */
    @Override
    public BaseResponse sendInternation(String mobile, String content) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 发送简单邮件
     * @param sendTo
     * @param title
     * @param content
     * @return
     */
    @Override
    public BaseResponse sendSimpleMail(String sendTo, String title, String content) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 发送模板邮件
     * @param sendTo
     * @param languageNum
     * @param templateNum
     * @param param
     * @return
     */
    @Override
    public BaseResponse sendTemplateMail(String sendTo, String languageNum, Status templateNum, Map<String, Object> param){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

}
