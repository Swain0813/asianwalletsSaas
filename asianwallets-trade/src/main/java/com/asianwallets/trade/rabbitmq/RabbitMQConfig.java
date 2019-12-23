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
}
