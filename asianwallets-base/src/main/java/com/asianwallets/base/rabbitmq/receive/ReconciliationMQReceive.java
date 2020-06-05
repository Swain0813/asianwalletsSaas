package com.asianwallets.base.rabbitmq.receive;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.CheckAccountMapper;
import com.asianwallets.base.dao.OrderRefundMapper;
import com.asianwallets.base.dao.OrdersMapper;
import com.asianwallets.base.feign.MessageFeign;
import com.asianwallets.base.rabbitmq.RabbitMQSender;
import com.asianwallets.base.service.ClearingService;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

/**
 * 调账相关队列
 */
@Component
@Slf4j
public class ReconciliationMQReceive {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    @Autowired
    private CheckAccountMapper checkAccountMapper;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private ClearingService clearingService;

    @Value("${custom.warning.mobile}")
    private String developerMobile;

    @Value("${custom.warning.email}")
    private String developerEmail;


    /**
     * 退款系统补单队列
     *
     * @param value
     */
    @RabbitListener(queues = "TC_MQ_FINANCE_TKBUDAN_DL")
    @Transactional
    public void processTZBD(String value) {
        log.info("----------------- TC_MQ_FINANCE_TKBUDAN_DL 系统补单  -------------- 退款单号 ： {}", value);
        OrderRefund orderRefund = orderRefundMapper.selectByPrimaryKey(value);
        orderRefund.setRefundStatus(TradeConstant.REFUND_SUCCESS);
        orderRefund.setRefundChannelNumber(checkAccountMapper.selectByOrderId(value));
        orderRefund.setRemark("系统补单成功");
        orderRefund.setUpdateTime(new Date());
        if (TradeConstant.REFUND_WAIT.equals(orderRefund.getRefundStatus())) {
            orderRefundMapper.updateByPrimaryKeySelective(orderRefund);
            checkAccountMapper.upateErrorType(value, "系统补单");
        }
    }

    /**
     * 收单系统补单队列
     *
     * @param value
     */
    @RabbitListener(queues = "TC_MQ_FINANCE_SDBUDAN_DL")
    @Transactional
    public void processSDBD(String value) {
        log.info("----------------- TC_MQ_FINANCE_SDBUDAN_DL 系统补单  -------------- 收单单号 ： {}", value);
        RabbitMassage rabbitMassage = JSON.parseObject(value, RabbitMassage.class);
        if (rabbitMassage.getCount() > 0) {
            rabbitMassage.setCount(rabbitMassage.getCount() - 1);//请求次数减一
            Orders orders = ordersMapper.selectByPrimaryKey(rabbitMassage.getValue());
            orders.setChannelNumber(checkAccountMapper.selectByOrderId(rabbitMassage.getValue()));
            orders.setRemark("系统补单成功");
            orders.setUpdateTime(new Date());
            if (TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
                //上报清结算 //收单
                FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                //上报清结算资金变动接口
                BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                if (!fundChangeResponse.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {
                    //业务处理失败
                    log.info("-----------------系统补单失败 上报队列 TC_MQ_FINANCE_SDBUDAN_DL -------------- rabbitMassage : {}", JSON.toJSONString(rabbitMassage));
                    rabbitMQSender.send(AD3MQConstant.TC_MQ_FINANCE_SDBUDAN_DL, JSON.toJSONString(rabbitMassage));
                    } else {
                        orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
                        ordersMapper.updateByPrimaryKeySelective(orders);
                        checkAccountMapper.upateErrorType(rabbitMassage.getValue(), "系统补单");
                    }
            }
        } else {
            //预警机制
            messageFeign.sendSimple(developerMobile, "saas-收单系统补单队列失败 TC_MQ_FINANCE_SDBUDAN_DL预警 ：{ " + value + " }");//短信通知
            messageFeign.sendSimpleMail(developerEmail, "saas-收单系统补单队列 TC_MQ_FINANCE_SDBUDAN_DL预警 ", "TC_MQ_FINANCE_SDBUDAN_DL预警 ：{ " + value + " }");//邮件通知
        }
    }

}
