package com.asianwallets.common.dto.th.demo1;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:54
 **/

import lombok.Data;

/**
 * 长度不正确异常
 */
@Data
public class IncorrectLengthException extends Exception{

    private String msg;

    public IncorrectLengthException(String msg) {
        this.msg = msg;
    }

}

