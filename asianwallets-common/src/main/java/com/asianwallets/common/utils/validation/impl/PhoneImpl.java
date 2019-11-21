package com.asianwallets.common.utils.validation.impl;

import com.asianwallets.asianwalletcommon.common.utils.validation.Phone;
import com.asianwallets.asianwalletcommon.common.utils.validation.regexp.PlatformRegexp;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @version v1.0.0
 * @classDesc: 类描述: 电话号码验证注解实现
 * @createTime 2018年3月14日 下午8:37:53
 * @copyright: 上海众哈网络技术有限公司
 */
public class PhoneImpl implements ConstraintValidator<Phone, String> {

    private boolean mobile;

    @Override
    public void initialize(Phone constraintAnnotation) {

        this.mobile = constraintAnnotation.mobile();

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isBlank(value)) {
            return false;
        }

        if ((this.mobile && PlatformRegexp.validate(PlatformRegexp.MOBILE, value))) {
            return true;
        }

        if (PlatformRegexp.validate(PlatformRegexp.PHONE, value)) {
            return true;
        }

        return false;
    }
}
