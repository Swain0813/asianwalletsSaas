package com.asianwallets.trade.rabbitmq.receive;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.feign.MessageFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.vo.FundChangeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FundChangeFailMQReceive {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private MessageFeign messageFeign;

    @Value("${custom.warning.mobile}")
    private String mobile;

    @Value("${custom.warning.email}")
    private String email;

    /**
     * 支付成功后上报清结算失败的队列
     *
     * @param value json 数据
     */
    @RabbitListener(queues = "MQ_PLACE_ORDER_FUND_CHANGE_FAIL")
    public void processOrderFundChangeFail(String value) {
        log.info("===============【支付成功后上报清结算失败的队列】===============【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】 value: {}", value);
        RabbitMassage rabbitMassage = JSON.parseObject(value, RabbitMassage.class);
        if (rabbitMassage.getCount() > 0) {
            rabbitMassage.setCount(rabbitMassage.getCount() - 1);
            Orders orders = JSON.parseObject(rabbitMassage.getValue(), Orders.class);
            //资金变动接口输入参数
            FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
            //上报清结算
            BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
            if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                log.info("=================【支付成功后上报清结算失败的队列】=================【上报清结算失败,继续上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
            }
        } else {
            messageFeign.sendSimple(mobile, "下单支付成功时上报清结算失败 MQ_PLACE_ORDER_FUND_CHANGE_FAIL 预警 :{ " + value + " }");
            messageFeign.sendSimpleMail(email, "下单支付成功时上报清结算失败 MQ_PLACE_ORDER_FUND_CHANGE_FAIL 预警", "MQ_PLACE_ORDER_FUND_CHANGE_FAIL 预警 ：{ " + value + " }");
        }
    }
}
