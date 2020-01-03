package com.asianwallets.trade.service.impl;

import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.service.ShareBenefitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-01-03 14:47
 **/
@Slf4j
@Service
@Transactional
public class ShareBenefitServiceImpl implements ShareBenefitService {

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/8/23
     * @Descripate 插入分润流水
     **/
    @Override
    public void insertShareBenefitLogs(String orderId) {
        log.error("================== 【insertShareBenefitLogs 插入分润流水异常】=================== orderId: 【{}】", orderId);
        try {
            Byte type;
            if (orderId.startsWith("O")) {
                //订单号以O开头是收款单
                type = 1;
            } else {
                type = 2;
            }
            

            if (type == 1) {
                /***************************************** 收款单的场合 ****************************************/


            }else{
                /***************************************** 付款单的场合 ****************************************/


            }
        } catch (Exception e) {
            log.error("================== 【insertShareBenefitLogs 插入分润流水异常】=================== orderId: 【{}】,Exception :【{}】", orderId, e);
            //rabbitMQSender.send(AD3MQConstant.MQ_FR_DL, orderId);
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }


}
