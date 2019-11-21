package com.asianwallets.common.utils.validation;

import com.asianwallets.asianwalletcommon.common.utils.validation.impl.NameImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @version v1.0.0
 * @classDesc: 类描述: 名称验证注解
 * @createTime 2018年3月14日 下午8:37:53
 * @copyright: 上海众哈网络技术有限公司
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {NameImpl.class})
public @interface Name {

    String message() default "名称不合法,最小值:{min},最大值:{max}";

    String charset() default "GBK";

    int charsetSize() default 2;

    /**
     * @return size the element must be higher or equal to
     */
    int min() default 0;

    /**
     * @return size the element must be lower or equal to
     */
    int max() default Integer.MAX_VALUE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
