package com.asianwallets.common.utils.validation;


import com.asianwallets.common.utils.validation.impl.PhoneImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @version v1.0.0
 * @classDesc: 类描述: 电话号码验证注解
 * @createTime 2018年3月14日 下午8:37:53
 * @copyright: 上海众哈网络技术有限公司
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {PhoneImpl.class})
public @interface Phone {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "无效的电话号码";

    boolean mobile() default true;

}
