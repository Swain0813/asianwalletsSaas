package com.asianwallets.trade.rabbitmq.receive;

import com.asianwallets.trade.service.ShareBenefitService;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-01-06 11:18
 **/
@Component
@Slf4j
public class ShareBenefitMQReceive {

    @Autowired
    private ShareBenefitService shareBenefitService;

    @RabbitListener(queues = "MQ_FR_DL")
    public void processFR(String value) {
        try {
            shareBenefitService.insertShareBenefitLogs(value);
        } catch (Exception e) {
            log.error("================== MQ_FR_DL ================ Exception :{}",e);
        }
    }

}
