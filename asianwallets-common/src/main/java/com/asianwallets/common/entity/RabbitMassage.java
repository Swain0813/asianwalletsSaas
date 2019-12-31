package com.asianwallets.common.entity;

import lombok.Data;

/**
 * @description: 上传rabbitmassage
 * @author: YangXu
 * @create: 2019-03-21 10:22
 **/
@Data
public class RabbitMassage {

    public String value; //请求内容
    public Integer count;//请求次数

    public RabbitMassage(int count, String value) {
        this.count = count;
        this.value = value;
    }
}
