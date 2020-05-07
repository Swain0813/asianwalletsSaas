package com.asianwallets.common.dto.th.demo;

import java.lang.annotation.*;

/**
 * ISO8583字段域注解类
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ISO8583Annotation {

    /**
     * 域索引  MUST
     * */
    int fldIndex();

    /**
     * 数据域长度    MUST
     * */
    int dataFldLength();

    /**
     * 域编码规则(ASCII/BCD/HEX/BINARY)   MUST
     * */
    String encodeRule();

    /**
     * 域字段标识(0: 不用 1: 长度固定 2: 2位变长 3: 3位变长)  MUST
     * */
    String fldFlag();

    /**
     * 域长度编码规则(ASCII/BCD[默认]/HEX/BINARY)
     * */
    String lenEncodeRule() default "BCD";

    /**
     * 域填充规则(NONE(默认)/AFTER/BEFORE)
     * */
    String fillRule() default "NONE";

    /**
     * 域填充字符,十六进制ASSIC码(0:30, 空格:20)
     * */
    String fillChar() default "";

    /**
     * 域默认值
     * */
    String defalutValue() default "";
}
