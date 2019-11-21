package com.asianwallets.asianwalletcommon.common.utils.validation.impl;

import com.asianwallets.asianwalletcommon.common.utils.validation.IdCard;
import com.asianwallets.asianwalletcommon.common.utils.validation.regexp.IdCardUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @version v1.0.0
 * @classDesc: 类描述: 身份证验证注解实现
 * @createTime 2018年3月14日 下午8:37:53
 * @copyright: 上海众哈网络技术有限公司
 */
public class IdCardImpl implements ConstraintValidator<IdCard, String> {

    @Override
    public void initialize(IdCard constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return IdCardUtils.isValid(value);
    }
}
