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

    /********************************************************* 分润队列 **********************************************************************/
    //RF or RV请求失败
    public final static String SAAS_FR_DL = AD3MQConstant.SAAS_FR_DL;

    @Bean
    public Queue SAAS_FR_DL() {
        return new Queue(RabbitMQConfig.SAAS_FR_DL);
    }
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
    public final static String MQ_PLACE_ORDER_FUND_CHANGE_FAIL = AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL;

    //上报清结算失败队列
    @Bean
    public Queue fundChangeFailQueue() {
        return new Queue(RabbitMQConfig.MQ_PLACE_ORDER_FUND_CHANGE_FAIL);
    }

    /* ===========================================【回调商户服务器失败队列】=============================================== */
    //回调失败队列
    public static final String MQ_AW_CALLBACK_URL_FAIL = AD3MQConstant.MQ_AW_CALLBACK_URL_FAIL;
    //回调失败队列死信队列
    public static final String E_MQ_AW_CALLBACK_URL_FAIL = AD3MQConstant.E_MQ_AW_CALLBACK_URL_FAIL;
    //回调失败队列路由Key
    public static final String MQ_AW_CALLBACK_URL_FAIL_KEY = AD3MQConstant.MQ_AW_CALLBACK_URL_FAIL_KEY;
    //回调失败队列交换机
    public static final String MQ_AW_CALLBACK_URL_FAIL_EXCHANGE = AD3MQConstant.MQ_AW_CALLBACK_URL_FAIL_EXCHANGE;

    //声明回调失败队列
    @Bean
    public Queue callBackFailQueue() {
        return new Queue(RabbitMQConfig.MQ_AW_CALLBACK_URL_FAIL, true, false, false);
    }

    //声明回调失败交换机
    @Bean
    public DirectExchange exchangeCallBack() {
        return new DirectExchange(MQ_AW_CALLBACK_URL_FAIL_EXCHANGE);
    }

    //绑定回调失败队列到回调失败交换机,并通过指定的RoutingKey路由
    @Bean
    public Binding deadLetterBindingCallBack() {
        return BindingBuilder.bind(callBackFailQueue()).to(exchangeCallBack()).with(MQ_AW_CALLBACK_URL_FAIL_KEY);
    }

    //回调失败队列的配置死信队列,即入队队列
    @Bean
    public Queue deadLetterCallBack() {
        Map<String, Object> args = new HashMap<>();
        //延迟队列2分钟
        args.put("x-message-ttl", 1000 * 60 * 2);
        args.put("x-dead-letter-exchange", MQ_AW_CALLBACK_URL_FAIL_EXCHANGE);
        args.put("x-dead-letter-routing-key", MQ_AW_CALLBACK_URL_FAIL_KEY);
        return new Queue(E_MQ_AW_CALLBACK_URL_FAIL, true, false, false, args);
    }


    /* ===========================================      NganLuong查询队列1      =============================================== */
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL = AD3MQConstant.MQ_NGANLUONG_CHECK_ORDER_DL;//NganLuong查询订单状态队列
    public static final String E_MQ_NGANLUONG_CHECK_ORDER_DL = AD3MQConstant.E_MQ_NGANLUONG_CHECK_ORDER_DL;//NganLuong查询订单死信队列
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_KEY = AD3MQConstant.MQ_NGANLUONG_CHECK_ORDER_DL_KEY;//NganLuong查询订单信息key
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE = AD3MQConstant.MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE;//NganLuong查询订单死信路由


    @Bean
    public Queue operateNGANLUONGQuery() {
        return new Queue(RabbitMQConfig.MQ_NGANLUONG_CHECK_ORDER_DL, true, false, false);
    }

    //用于NL通道查询队列的死信路由
    @Bean
    public DirectExchange exchangeNGANLUONGQuery() {
        return new DirectExchange(MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE);
    }

    //NL通道查询队列的绑定exchange 到出队队列
    @Bean
    public Binding deadLetterBindingNGANLUONGuery() {
        return BindingBuilder.bind(operateNGANLUONGQuery()).to(exchangeNGANLUONGQuery()).with(MQ_NGANLUONG_CHECK_ORDER_DL_KEY);
    }

    //NL通道查询队列的配置死信队列,即入队队列
    @Bean
    public Queue deadLetterQueueNGANLUONGuery() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1000 * 60 * 5);//延迟队列5分钟
        args.put("x-dead-letter-exchange", MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE);
        args.put("x-dead-letter-routing-key", MQ_NGANLUONG_CHECK_ORDER_DL_KEY);
        return new Queue(E_MQ_NGANLUONG_CHECK_ORDER_DL, true, false, false, args);
    }
    /* ===========================================      NganLuong查询队列2      =============================================== */
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL2 = AD3MQConstant.MQ_NGANLUONG_CHECK_ORDER_DL2;//NganLuong查询订单状态队列2
    public static final String E_MQ_NGANLUONG_CHECK_ORDER_DL2 = AD3MQConstant.E_MQ_NGANLUONG_CHECK_ORDER_DL2;//NganLuong查询订单死信队列
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_KEY2 = AD3MQConstant.MQ_NGANLUONG_CHECK_ORDER_DL_KEY2;//NganLuong查询订单信息key
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE2 = AD3MQConstant.MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE2;//NganLuong查询订单死信路由

    @Bean
    public Queue operateNGANLUONGQuery2() {
        return new Queue(RabbitMQConfig.MQ_NGANLUONG_CHECK_ORDER_DL2, true, false, false);
    }

    //用于NL通道查询队列的死信路由2
    @Bean
    public DirectExchange exchangeNGANLUONGQuery2() {
        return new DirectExchange(MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE2);
    }

    //NL通道查询队列的绑定exchange2 到出队队列
    @Bean
    public Binding deadLetterBindingNGANLUONGuery2() {
        return BindingBuilder.bind(operateNGANLUONGQuery2()).to(exchangeNGANLUONGQuery2()).with(MQ_NGANLUONG_CHECK_ORDER_DL_KEY2);
    }

    //NL通道查询队列的配置死信队列2,即入队队列
    @Bean
    public Queue deadLetterQueueNGANLUONGuery2() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1000 * 60 * 30);//延迟队列5分钟
        args.put("x-dead-letter-exchange", MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE2);
        args.put("x-dead-letter-routing-key", MQ_NGANLUONG_CHECK_ORDER_DL_KEY2);
        return new Queue(E_MQ_NGANLUONG_CHECK_ORDER_DL2, true, false, false, args);
    }

    /* ===========================================      MegaPay-THB查询队列1      =============================================== */
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER = AD3MQConstant.MQ_MEGAPAY_THB_CHECK_ORDER;//MegaPay-THB查询队列1
    public static final String E_MQ_MEGAPAY_THB_CHECK_ORDER = AD3MQConstant.E_MQ_MEGAPAY_THB_CHECK_ORDER;//MegaPay-THB查询死信队列1
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_KEY = AD3MQConstant.MQ_MEGAPAY_THB_CHECK_ORDER_KEY;//MegaPay-THB查询死信队列1路由
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE = AD3MQConstant.MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE;//MegaPay-THB查询死信队列1交换机


    @Bean
    public Queue operateMegaPayTHBQuery() {
        return new Queue(RabbitMQConfig.MQ_MEGAPAY_THB_CHECK_ORDER, true, false, false);
    }

    //用于MegaPayTHB通道查询队列的死信路由
    @Bean
    public DirectExchange exchangeMegaPayTHBQuery() {
        return new DirectExchange(MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE);
    }

    //MegaPayTHB通道查询队列的绑定exchange 到出队队列
    @Bean
    public Binding deadLetterBindingMegaPayTHBQuery() {
        return BindingBuilder.bind(operateMegaPayTHBQuery()).to(exchangeMegaPayTHBQuery()).with(MQ_MEGAPAY_THB_CHECK_ORDER_KEY);
    }

    //MegaPayTHB通道查询队列的配置死信队列,即入队队列
    @Bean
    public Queue deadLetterQueueMegaPayTHBQuery() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1000 * 60 * 5);//延迟队列5分钟
        args.put("x-dead-letter-exchange", MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE);
        args.put("x-dead-letter-routing-key", MQ_MEGAPAY_THB_CHECK_ORDER_KEY);
        return new Queue(E_MQ_MEGAPAY_THB_CHECK_ORDER, true, false, false, args);
    }
    /* ===========================================      MegaPay-THB查询队列2      =============================================== */
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER2 = AD3MQConstant.MQ_MEGAPAY_THB_CHECK_ORDER2;//MegaPay-THB查询队列2
    public static final String E_MQ_MEGAPAY_THB_CHECK_ORDER2 = AD3MQConstant.E_MQ_MEGAPAY_THB_CHECK_ORDER2;//MegaPay-THB查询死信队列2
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_KEY2 = AD3MQConstant.MQ_MEGAPAY_THB_CHECK_ORDER_KEY2;//MegaPay-THB查询死信队列2路由
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE2 = AD3MQConstant.MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE2;//MegaPay-THB查询死信队列2交换机


    @Bean
    public Queue operateMegaPayTHBQuery2() {
        return new Queue(RabbitMQConfig.MQ_MEGAPAY_THB_CHECK_ORDER2, true, false, false);
    }

    //用于MegaPayTHB通道查询队列2的死信路由
    @Bean
    public DirectExchange exchangeMegaPayTHBQuery2() {
        return new DirectExchange(MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE2);
    }

    //MegaPayTHB通道查询队列2的绑定exchange 到出队队列
    @Bean
    public Binding deadLetterBindingMegaPayTHBQuery2() {
        return BindingBuilder.bind(operateMegaPayTHBQuery2()).to(exchangeMegaPayTHBQuery2()).with(MQ_MEGAPAY_THB_CHECK_ORDER_KEY2);
    }

    //MegaPayTHB通道查询队列2的配置死信队列,即入队队列
    @Bean
    public Queue deadLetterQueueMegaPayTHBQuery2() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1000 * 60 * 30);//延迟队列5分钟
        args.put("x-dead-letter-exchange", MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE2);
        args.put("x-dead-letter-routing-key", MQ_MEGAPAY_THB_CHECK_ORDER_KEY2);
        return new Queue(E_MQ_MEGAPAY_THB_CHECK_ORDER2, true, false, false, args);
    }


    /* ===========================================      Qfpay Refunding 查询队列      =============================================== */
    public static final String MQ_QFPAY_REFUND_SEARCH = AD3MQConstant.MQ_QFPAY_REFUND_SEARCH;//MegaPay-THB查询队列2
    public static final String E_MQ_QFPAY_REFUND_SEARCH = AD3MQConstant.E_MQ_QFPAY_REFUND_SEARCH;//MegaPay-THB查询死信队列2
    public static final String MQ_QFPAY_REFUND_SEARCH_KEY = AD3MQConstant.MQ_QFPAY_REFUND_SEARCH_KEY;//MegaPay-THB查询死信队列2路由
    public static final String MQ_QFPAY_REFUND_SEARCH_EXCHANGE = AD3MQConstant.MQ_QFPAY_REFUND_SEARCH_EXCHANGE;//MegaPay-THB查询死信队列2交换机


    @Bean
    public Queue operateMQ_QFPAY_REFUND_SEARCH() {
        return new Queue(RabbitMQConfig.MQ_QFPAY_REFUND_SEARCH, true, false, false);
    }

    @Bean
    public DirectExchange exchangeMQ_QFPAY_REFUND_SEARCH() {
        return new DirectExchange(MQ_QFPAY_REFUND_SEARCH_EXCHANGE);
    }

    @Bean
    public Binding deadLetterBindingMQ_QFPAY_REFUND_SEARCH() {
        return BindingBuilder.bind(operateMQ_QFPAY_REFUND_SEARCH()).to(exchangeMQ_QFPAY_REFUND_SEARCH()).with(MQ_QFPAY_REFUND_SEARCH_KEY);
    }

    @Bean
    public Queue deadLetterQueueMQ_QFPAY_REFUND_SEARCH() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1000 * 60 * 30);//延迟队列5分钟
        args.put("x-dead-letter-exchange", MQ_QFPAY_REFUND_SEARCH_EXCHANGE);
        args.put("x-dead-letter-routing-key", MQ_QFPAY_REFUND_SEARCH_KEY);
        return new Queue(E_MQ_QFPAY_REFUND_SEARCH, true, false, false, args);
    }

    /* ===========================================     Qfpay Canneling 查询队列      =============================================== */
    public static final String MQ_QFPAY_CANNEL_SEARCH = AD3MQConstant.MQ_QFPAY_CANNEL_SEARCH;//MegaPay-THB查询队列2
    public static final String E_MQ_QFPAY_CANNEL_SEARCH = AD3MQConstant.E_MQ_QFPAY_CANNEL_SEARCH;//MegaPay-THB查询死信队列2
    public static final String MQ_QFPAY_CANNEL_SEARCH_KEY = AD3MQConstant.MQ_QFPAY_CANNEL_SEARCH_KEY;//MegaPay-THB查询死信队列2路由
    public static final String MQ_QFPAY_CANNEL_SEARCH_EXCHANGE = AD3MQConstant.MQ_QFPAY_CANNEL_SEARCH_EXCHANGE;//MegaPay-THB查询死信队列2交换机

    @Bean
    public Queue operateMQ_QFPAY_CANNEL_SEARCH() {
        return new Queue(RabbitMQConfig.MQ_QFPAY_CANNEL_SEARCH, true, false, false);
    }

    @Bean
    public DirectExchange exchangeMQ_QFPAY_CANNEL_SEARCH() {
        return new DirectExchange(MQ_QFPAY_CANNEL_SEARCH_EXCHANGE);
    }

    @Bean
    public Binding deadLetterBindingMQ_QFPAY_CANNEL_SEARCH() {
        return BindingBuilder.bind(operateMQ_QFPAY_CANNEL_SEARCH()).to(exchangeMQ_QFPAY_CANNEL_SEARCH()).with(MQ_QFPAY_CANNEL_SEARCH_KEY);
    }

    @Bean
    public Queue deadLetterQueueMQ_QFPAY_CANNEL_SEARCH() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1000 * 60 * 30);//延迟队列5分钟
        args.put("x-dead-letter-exchange", MQ_QFPAY_CANNEL_SEARCH_EXCHANGE);
        args.put("x-dead-letter-routing-key", MQ_QFPAY_CANNEL_SEARCH_KEY);
        return new Queue(E_MQ_QFPAY_CANNEL_SEARCH, true, false, false, args);
    }

    /* ===========================================     Qfpay Canneling2 查询队列      =============================================== */
    public static final String MQ_QFPAY_CANNEL_SEARCH2 = AD3MQConstant.MQ_QFPAY_CANNEL_SEARCH2;//MegaPay-THB查询队列2
    public static final String E_MQ_QFPAY_CANNEL_SEARCH2 = AD3MQConstant.E_MQ_QFPAY_CANNEL_SEARCH2;//MegaPay-THB查询死信队列2
    public static final String MQ_QFPAY_CANNEL_SEARCH_KEY2 = AD3MQConstant.MQ_QFPAY_CANNEL_SEARCH_KEY2;//MegaPay-THB查询死信队列2路由
    public static final String MQ_QFPAY_CANNEL_SEARCH_EXCHANGE2 = AD3MQConstant.MQ_QFPAY_CANNEL_SEARCH_EXCHANGE2;//MegaPay-THB查询死信队列2交换机

    @Bean
    public Queue operateMQ_QFPAY_CANNEL_SEARCH2() {
        return new Queue(RabbitMQConfig.MQ_QFPAY_CANNEL_SEARCH2, true, false, false);
    }

    @Bean
    public DirectExchange exchangeMQ_QFPAY_CANNEL_SEARCH2() {
        return new DirectExchange(MQ_QFPAY_CANNEL_SEARCH_EXCHANGE2);
    }

    @Bean
    public Binding deadLetterBindingMQ_QFPAY_CANNEL_SEARCH2() {
        return BindingBuilder.bind(operateMQ_QFPAY_CANNEL_SEARCH2()).to(exchangeMQ_QFPAY_CANNEL_SEARCH2()).with(MQ_QFPAY_CANNEL_SEARCH_KEY2);
    }

    @Bean
    public Queue deadLetterQueueMQ_QFPAY_CANNEL_SEARCH2() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1000 * 60 * 30);//延迟队列5分钟
        args.put("x-dead-letter-exchange", MQ_QFPAY_CANNEL_SEARCH_EXCHANGE2);
        args.put("x-dead-letter-routing-key", MQ_QFPAY_CANNEL_SEARCH_KEY2);
        return new Queue(E_MQ_QFPAY_CANNEL_SEARCH2, true, false, false, args);
    }


}
