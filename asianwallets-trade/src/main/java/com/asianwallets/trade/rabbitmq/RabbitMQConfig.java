package com.asianwallets.trade.rabbitmq;

import com.asianwallets.common.constant.AD3MQConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-20 14:55
 **/
@Configuration
public class RabbitMQConfig {



    /********************************************************  退款接口相关队列 **********************************************************************/
    //退款RF请求失败
    public final static String TK_RF_FAIL_DL = AD3MQConstant.TK_RF_FAIL_DL;
    @Bean
    public Queue TK_RF_FAIL_DL() {
        return new Queue(RabbitMQConfig.TK_RF_FAIL_DL);
    }
    //调账失败队列
    public final static String RA_AA_FAIL_DL = AD3MQConstant.RA_AA_FAIL_DL;
    @Bean
    public Queue RA_AA_FAIL_DL() {
        return new Queue(RabbitMQConfig.RA_AA_FAIL_DL);
    }
}
