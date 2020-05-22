package com.asianwallets.base.rabbitmq;

import com.asianwallets.common.constant.AD3MQConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Configuration
public class RabbitMQConfig {

    /* ===========================================【报备失败队列】=============================================== */
    //报备失败队列
    public static final String MQ_REPORT_FAIL = AD3MQConstant.MQ_REPORT_FAIL;
    //报备失败队列死信队列
    public static final String E_MQ_REPORT_FAIL = AD3MQConstant.E_MQ_REPORT_FAIL;
    //报备失败队列路由Key
    public static final String MQ_REPORT_FAIL_KEY = AD3MQConstant.MQ_REPORT_FAIL_KEY;
    //报备失败队列交换机
    public static final String MQ_REPORT_FAIL_EXCHANGE = AD3MQConstant.MQ_REPORT_FAIL_EXCHANGE;

    //声明报备失败队列
    @Bean
    public Queue callBackFailQueue() {
        return new Queue(RabbitMQConfig.MQ_REPORT_FAIL, true, false, false);
    }

    //声明报备失败交换机
    @Bean
    public DirectExchange exchangeCallBack() {
        return new DirectExchange(MQ_REPORT_FAIL_EXCHANGE);
    }

    //绑定报备失败队列到报备失败交换机,并通过指定的RoutingKey路由
    @Bean
    public Binding deadLetterBindingCallBack() {
        return BindingBuilder.bind(callBackFailQueue()).to(exchangeCallBack()).with(MQ_REPORT_FAIL_KEY);
    }

    //报备失败队列的配置死信队列,即入队队列
    @Bean
    public Queue deadLetterCallBack() {
        Map<String, Object> args = new HashMap<>();
        //延迟队列30分钟
        args.put("x-message-ttl", 1000 * 60 * 30);
        args.put("x-dead-letter-exchange", MQ_REPORT_FAIL_EXCHANGE);
        args.put("x-dead-letter-routing-key", MQ_REPORT_FAIL_KEY);
        return new Queue(E_MQ_REPORT_FAIL, true, false, false, args);
    }





}
