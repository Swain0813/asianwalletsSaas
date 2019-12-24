package com.asianwallets.trade.rabbitmq;

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
 * @description:
 * @author: YangXu
 * @create: 2019-12-20 14:55
 **/
@Configuration
public class RabbitMQConfig {


    /********************************************************  退款接口相关队列 **********************************************************************/
    //RF or RV请求失败
    public final static String RV_RF_FAIL_DL = AD3MQConstant.RV_RF_FAIL_DL;

    @Bean
    public Queue RV_RF_FAIL_DL() {
        return new Queue(RabbitMQConfig.RV_RF_FAIL_DL);
    }

    //调账失败队列
    public final static String RA_AA_FAIL_DL = AD3MQConstant.RA_AA_FAIL_DL;

    @Bean
    public Queue RA_AA_FAIL_DL() {
        return new Queue(RabbitMQConfig.RA_AA_FAIL_DL);
    }

    //退款上报失败队列
    public final static String TK_SB_FAIL_DL = AD3MQConstant.TK_SB_FAIL_DL;

    @Bean
    public Queue TK_SB_FAIL_DL() {
        return new Queue(RabbitMQConfig.TK_SB_FAIL_DL);
    }

    //撤销更新失败死信队列
    public final static String CX_GX_FAIL_DL = AD3MQConstant.CX_GX_FAIL_DL;
    public final static String E_CX_GX_FAIL_DL = AD3MQConstant.E_CX_GX_FAIL_DL;
    public final static String CX_GX_FAIL_DL_KEY = AD3MQConstant.CX_GX_FAIL_DL_KEY;
    public final static String CX_GX_FAIL_DL_EXCHANGE = AD3MQConstant.CX_GX_FAIL_DL_EXCHANGE;

    //用于延时撤销付款中订单查询AD3订单信息消费的队列
    @Bean
    public Queue operateCX_GX_FAIL_DL() {
        return new Queue(RabbitMQConfig.CX_GX_FAIL_DL, true, false, false);
    }

    //用于撤销ad3订单查询队列的死信路由
    @Bean
    public DirectExchange exchangeCX_GX_FAIL_DL() {
        return new DirectExchange(CX_GX_FAIL_DL_EXCHANGE);
    }

    //撤销AD3订单查询的绑定exchange 到出队队列
    @Bean
    public Binding deadLetterBindingCX_GX_FAIL_DL() {
        return BindingBuilder.bind(operateCX_GX_FAIL_DL()).to(exchangeCX_GX_FAIL_DL()).with(CX_GX_FAIL_DL_KEY);
    }

    //撤销AD3订单查询的配置死信队列，即入队队列
    @Bean
    public Queue deadLetterQueueCX_GX_FAIL_DL() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1000 * 60 * 5);
        args.put("x-dead-letter-exchange", CX_GX_FAIL_DL_EXCHANGE);
        args.put("x-dead-letter-routing-key", CX_GX_FAIL_DL_KEY);
        return new Queue(E_CX_GX_FAIL_DL, true, false, false, args);
    }

    //撤销上报上游失败
    public final static String CX_SB_FAIL_DL = AD3MQConstant.CX_SB_FAIL_DL;

    @Bean
    public Queue CX_SB_FAIL_DL() {
        return new Queue(RabbitMQConfig.CX_SB_FAIL_DL);
    }

    //=================================【支付成功后上报清结算失败队列】==========================================================
//    public final static String MQ_PLACE_ORDER_FUND_CHANGE_FAIL = AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL;
//
//
//    @Bean
//    public Queue operateRecordPlaceOrderFundChangeFail() {
//        return new Queue(RabbitMQConfig.MQ_PLACE_ORDER_FUND_CHANGE_FAIL);
//    }
}
