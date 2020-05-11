package com.asianwallets.common.dto.th.ISO8583;

/**
 * 域字段标识
 */
public enum FldFlag {

    /** 固定长度 */
    FIXED,
    /** 2位变长 */
    UNFIXED_2,
    /** 3位变长 */
    UNFIXED_3,

    BCD,

    ASC,

    BINERY

}

