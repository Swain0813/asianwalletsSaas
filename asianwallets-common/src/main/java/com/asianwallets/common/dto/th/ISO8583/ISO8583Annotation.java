package com.asianwallets.common.dto.th.ISO8583;

import java.lang.annotation.*;

/**
 * ISO8583字段域注解类
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ISO8583Annotation {

    /**
     * 域索引
     * */
    int fldIndex();

    /**
     * 域字段标识
     * FIXED:固定长度；UNFIXED_2:2位变长；UNFIXED_3:3位变长
     * */
    FldFlag fldFlag();

    /**
     * 数据域长度
     * */
    int dataFldLength();

    /**
     * 类型
     * BCD,ASC,BINERY
     * */
    String type();

}
