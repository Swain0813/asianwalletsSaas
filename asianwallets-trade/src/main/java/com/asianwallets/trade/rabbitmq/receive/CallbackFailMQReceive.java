package com.asianwallets.trade.rabbitmq.receive;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.feign.MessageFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.vo.OnlineCallbackURLVO;
import com.asianwallets.trade.vo.OnlineCallbackVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallbackFailMQReceive {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private OrdersMapper ordersMapper;

    @Value("${custom.warning.mobile}")
    private String mobile;

    @Value("${custom.warning.email}")
    private String email;

    /**
     * 回调商户服务器失败队列
     *
     * @param value json数据
     */
    @RabbitListener(queues = "MQ_AW_CALLBACK_URL_FAIL")
    public void processAWCU(String value) {
        RabbitMassage rabbitMassage = JSON.parseObject(value, RabbitMassage.class);
        OnlineCallbackURLVO onlineCallbackURLVO = JSON.parseObject(rabbitMassage.getValue(), OnlineCallbackURLVO.class);
        OnlineCallbackVO onlineCallbackVO = onlineCallbackURLVO.getOnlineCallbackVO();
        log.info("==================【回调商户队列信息记录】==================【商户回调接口URL记录】  serverUrl: {}", onlineCallbackURLVO.getReturnUrl());
        log.info("==================【回调商户队列信息记录】==================【回调参数记录】 OnlineCallbackVO: {} ", JSON.toJSONString(onlineCallbackVO));
        if (rabbitMassage.getCount() > 0) {
            rabbitMassage.setCount(rabbitMassage.getCount() - 1);
            try {
                cn.hutool.http.HttpResponse execute = HttpRequest.post(onlineCallbackURLVO.getReturnUrl())
                        .header(Header.CONTENT_TYPE, "application/json")
                        .body(JSON.toJSONString(onlineCallbackVO))
                        .timeout(30000)
                        .execute();
                String body = execute.body();
                log.info("==================【回调商户服务器】==================【HTTP状态码】status: {} |【响应结果记录】 body: {}", execute.getStatus(), body);
                if (StringUtils.isEmpty(body) || !body.equalsIgnoreCase(AsianWalletConstant.CALLBACK_SUCCESS)) {
                    log.info("==================【回调商户队列信息记录】==================【商户响应失败,继续上报商户回调队列】 MQ_AW_CALLBACK_URL_FAIL");
                    rabbitMQSender.send(AD3MQConstant.E_MQ_AW_CALLBACK_URL_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } catch (Exception e) {
                log.info("==================【回调商户队列信息记录】==================【httpException异常,继续上报商户回调队列】 MQ_AW_CALLBACK_URL_FAIL", e);
                rabbitMQSender.send(AD3MQConstant.E_MQ_AW_CALLBACK_URL_FAIL, JSON.toJSONString(rabbitMassage));
            }
        } else {
            log.info("==================【回调商户队列信息记录】==================【三次回调完成,回调商户失败】");
            //回调下游商户失败原因
            ordersMapper.updateOrderRemark(onlineCallbackVO.getReferenceNo(), "商户回调返回消息不为SUCCESS");
            messageFeign.sendSimple(mobile, "回调商户失败 MQ_AW_CALLBACK_URL_FAIL 预警 ：{ " + value + " }");//短信通知
            messageFeign.sendSimpleMail(email, "回调商户失败 MQ_AW_CALLBACK_URL_FAIL 预警", "MQ_AW_CALLBACK_URL_FAIL 预警 ：{ " + value + " }");//邮件通知
        }
    }

}
