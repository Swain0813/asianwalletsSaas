package com.asianwallets.base.rabbitmq.receive;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.MerchantReportMapper;
import com.asianwallets.base.feign.MessageFeign;
import com.asianwallets.base.rabbitmq.RabbitMQSender;
import com.asianwallets.base.service.AlipaySecmerchantReport;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.MerchantReport;
import com.asianwallets.common.utils.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class ReportFailMQReceive {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private MerchantReportMapper merchantReportMapper;

    @Autowired
    private AlipaySecmerchantReport alipaySecmerchantReport;

    @Value("${custom.warning.mobile}")
    private String mobile;

    @Value("${custom.warning.email}")
    private String email;

    /**
     * 回调商户服务器失败队列
     *
     * @param value json数据
     */
    @RabbitListener(queues = "MQ_REPORT_FAIL")
    public void processAWCU(String value) {
        RabbitMassage rabbitMassage = JSON.parseObject(value, RabbitMassage.class);
        MerchantReport merchantReport = JSON.parseObject(rabbitMassage.getValue(), MerchantReport.class);
        if (rabbitMassage.getCount() > 0) {
            rabbitMassage.setCount(rabbitMassage.getCount() - 1);
            try {
                HttpResponse httpResponse = alipaySecmerchantReport.getHttpResponse(merchantReport);
                if (httpResponse == null) {
                    log.info("==================【回调商户队列信息记录】==================【商户响应失败,继续上报商户回调队列】 MQ_AW_CALLBACK_URL_FAIL");
                    rabbitMQSender.send(AD3MQConstant.E_MQ_REPORT_FAIL, JSON.toJSONString(rabbitMassage));
                } else {
                    Map<String, Object> xmlMap = XmlUtil.xmlToMap(httpResponse.getStringResult());
                    if (xmlMap.get("is_success").equals("T")) {
                        //正确 更新数据
                        merchantReport.setEnabled(true);
                        merchantReport.setUpdateTime(new Date());
                        merchantReportMapper.updateByPrimaryKeySelective(merchantReport);
                    } else {
                        //错误 更新数据 发消息提醒错误
                        String msg = (String) xmlMap.get("error");
                        merchantReport.setEnabled(false);
                        merchantReport.setRemark(msg);
                        merchantReportMapper.updateByPrimaryKeySelective(merchantReport);
                    }
                }
            } catch (Exception e) {
                log.info("==================【支付宝报备失败】==================", e);
                rabbitMQSender.send(AD3MQConstant.MQ_REPORT_FAIL, JSON.toJSONString(rabbitMassage));
            }
        } else {
            messageFeign.sendSimple(mobile, "SAAS-支付宝报备失败 MQ_REPORT_FAIL 预警 ：{ " + value + " }");//短信通知
            messageFeign.sendSimpleMail(email, "SAAS-支付宝报备失败 MQ_REPORT_FAIL 预警", "MQ_REPORT_FAIL 预警 ：{ " + value + " }");//邮件通知
        }
    }

}
