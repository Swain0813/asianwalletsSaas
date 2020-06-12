package com.asianwallets.rights.rabbitmq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ消息发送
 */
@Component
@Slf4j
public class RabbitMQSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送信息
     * @param queue
     * @param msg
     */
    public void send(String queue, String msg) {
        log.info("------------------ RabbitMQSender send -----------------：queue:{},msg :{}", queue, JSON.toJSONString(msg));
        this.amqpTemplate.convertAndSend(queue, msg);
    }

}
