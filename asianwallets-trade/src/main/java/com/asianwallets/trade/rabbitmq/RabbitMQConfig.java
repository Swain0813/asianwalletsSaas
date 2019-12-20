package com.asianwallets.trade.rabbitmq;

import com.asianwallets.common.constant.AD3MQConstant;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-20 14:55
 **/
@Configuration
public class RabbitMQConfig {



    /********************************************************  退款接口相关队列 **********************************************************************/
    //退款请求失败
    public final static String TK_RF_FAIL_DL = AD3MQConstant.TK_RF_FAIL_DL;
}
