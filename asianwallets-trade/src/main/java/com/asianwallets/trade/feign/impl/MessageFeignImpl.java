package com.asianwallets.trade.feign.impl;

import com.asianwallets.common.enums.Status;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.trade.feign.MessageFeign;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageFeignImpl implements MessageFeign {

    @Override
    public BaseResponse sendSimpleMail(String sendTo, String title, String content) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse sendSimple(String mobile, String content) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse sendTemplateMail(String sendTo, String languageNum, Status templateNum, Map<String, Object> param) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
