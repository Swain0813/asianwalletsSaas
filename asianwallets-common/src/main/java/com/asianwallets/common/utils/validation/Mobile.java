package com.asianwallets.common.utils.validation;

import com.asianwallets.asianwalletcommon.common.utils.validation.impl.MobileImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @version v1.0.0
 * @classDesc: 类描述: 手机号码验证注解
 * @createTime 2018年3月14日 下午8:37:53
 * @copyright: 上海众哈网络技术有限公司
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {MobileImpl.class})
public @interface Mobile {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "无效的手机号码";

}
