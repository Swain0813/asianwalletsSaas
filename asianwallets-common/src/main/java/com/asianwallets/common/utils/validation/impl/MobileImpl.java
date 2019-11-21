package com.asianwallets.common.utils.validation.impl;

import com.asianwallets.common.utils.validation.Mobile;
import com.asianwallets.common.utils.validation.regexp.PlatformRegexp;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * @version v1.0.0
 * @classDesc: 类描述: 手机号码验证注解实现
 * @createTime 2018年3月14日 下午8:37:53
 * @copyright: 上海众哈网络技术有限公司
 */
public class MobileImpl implements ConstraintValidator<Mobile, String> {

    @Override
    public void initialize(Mobile constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isBlank(value)) {
            return false;
        }

        if (PlatformRegexp.validate(PlatformRegexp.MOBILE, value)) {
            return true;
        }

        return false;
    }
}
