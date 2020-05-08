package com.asianwallets.common.dto.th.demo1;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:54
 **/

import lombok.Data;

/**
 * 报文格式不正确异常
 */
@Data
public class IncorrectMessageException  extends Exception{
    private String msg;

    public IncorrectMessageException(String msg) {
        this.msg = msg;
    }

}
