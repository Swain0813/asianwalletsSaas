package com.asianwallets.common.dto;

import lombok.Data;

/**
 * @description: 上传rabbitmassage
 * @author: YangXu
 * @create: 2019-03-21 10:22
 **/
@Data
public class RabbitMassage {

    //请求内容
    public String value;
    //请求次数
    public Integer count;

    public RabbitMassage(int count, String value) {
        this.count = count;
        this.value = value;
    }
}
