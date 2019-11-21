package com.asianwallets.common.utils.validation.impl;

import com.asianwallets.common.utils.validation.Name;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.UnsupportedEncodingException;

import static java.lang.Integer.parseInt;

/**
 * @version v1.0.0
 * @classDesc: 类描述: 名称验证注解实现
 * @createTime 2018年3月14日 下午8:37:53
 * @copyright: 上海众哈网络技术有限公司
 */
@Slf4j
public class NameImpl implements ConstraintValidator<Name, String> {
    @Override
    public void initialize(Name constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isBlank(value)) {
            return false;
        }

        try {
            ConstraintValidatorContextImpl cvc = (ConstraintValidatorContextImpl) context;

            int length = value.getBytes(cvc.getConstraintDescriptor().getAttributes().get("charset").toString()).length;

            int charsetSize = Integer.parseInt(cvc.getConstraintDescriptor().getAttributes().get("charsetSize").toString());

            Object min = cvc.getConstraintDescriptor().getAttributes().get("min");
            Object max = cvc.getConstraintDescriptor().getAttributes().get("max");

            return (length >= (parseInt(min.toString()))) && (length <= charsetSize * Long.parseLong(max.toString()));

        } catch (UnsupportedEncodingException e) {
            log.error("[NameImpl-isValid异常信息：]-{}", e);
        }

        return false;
    }
}
