package com.asianwallets.asianwalletcommon.common.utils.validation;


import com.asianwallets.asianwalletcommon.common.utils.validation.impl.IdCardImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @version v1.0.0
 * @classDesc: 类描述: 身份证验证注解
 * @createTime 2018年3月14日 下午8:37:53
 * @copyright: 上海众哈网络技术有限公司
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IdCardImpl.class})
public @interface IdCard {
    String message() default "身份证号码不合法";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
