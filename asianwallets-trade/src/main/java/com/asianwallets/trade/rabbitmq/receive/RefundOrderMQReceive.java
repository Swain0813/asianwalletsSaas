package com.asianwallets.trade.rabbitmq.receive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @description: 退款消费队列
 * @author: YangXu
 * @create: 2019-12-20 15:04
 **/
@Component
@Slf4j
public class RefundOrderMQReceive {


    @RabbitListener(queues = "TK_RF_FAIL_DL")
    public void processTZSB(String value) {

    }
}
