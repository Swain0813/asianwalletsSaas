package com.asianwallets.trade.utils;

import java.lang.annotation.*;

/**
 * 处理器类型注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface HandlerType {

    String[] value();

}
