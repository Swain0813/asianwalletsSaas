package com.asianwallets.trade.channels.nextpos.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.megapay.NextPosRefundDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.nextpos.NextPosService;
import com.asianwallets.trade.dao.OrderRefundMapper;
import com.asianwallets.trade.dao.ReconciliationMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-19 16:47
 **/
@Slf4j
@Service
@Transactional
public class NextPosServiceImpl extends ChannelsAbstractAdapter implements NextPosService {

    @Autowired
    private ChannelsFeign channelsFeign;
    @Autowired
    private OrderRefundMapper orderRefundMapper;
    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private ReconciliationMapper reconciliationMapper;
    @Autowired
    private ClearingService clearingService;
    @Autowired
    private RabbitMQSender rabbitMQSender;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     **/
    @Override
    public BaseResponse refund(Channel channel, OrderRefund orderRefund) {
        BaseResponse baseResponse = new BaseResponse();
        NextPosRefundDTO nextPosRefundDTO = new NextPosRefundDTO(orderRefund, channel);
        log.info("=================【NextPos退款】=================【请求Channels服务NextPos退款】请求参数 nextPosRefundDTO: {} ", JSON.toJSONString(nextPosRefundDTO));
        BaseResponse response = channelsFeign.nextPosRefund(nextPosRefundDTO);
        log.info("=================【NextPos退款】=================【Channels服务响应】 response: {} ", JSON.toJSONString(response));
        if (response.getCode().equals(TradeConstant.HTTP_SUCCESS)) {
            //请求成功
            Map<String, Object> respMap = (Map<String, Object>) response.getData();
            if (response.getMsg().equals(TradeConstant.HTTP_SUCCESS_MSG)) {
                //退款成功
                orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_SUCCESS, String.valueOf(respMap.get("transactionID")), null);
                //改原订单状态
                commonBusinessService.updateOrderRefundSuccess(orderRefund);
            } else {
                //退款失败
                baseResponse.setMsg(EResultEnum.REFUND_FAIL.getCode());
                Reconciliation reconciliation = commonBusinessService.createReconciliation(TradeConstant.AA,orderRefund, TradeConstant.REFUND_FAIL_RECONCILIATION);
                reconciliationMapper.insert(reconciliation);
                FundChangeDTO fundChangeDTO = new FundChangeDTO(TradeConstant.AA,reconciliation);
                BaseResponse cFundChange = clearingService.fundChange(fundChangeDTO);
                if (cFundChange.getCode().equals(TradeConstant.CLEARING_SUCCESS)) {//请求成功
                    orderRefundMapper.updateStatuts(orderRefund.getId(), TradeConstant.REFUND_FALID, null, null);
                    reconciliationMapper.updateStatusById(reconciliation.getId(), TradeConstant.RECONCILIATION_SUCCESS);
                    //改原订单状态
                    commonBusinessService.updateOrderRefundFail(orderRefund);
                } else {
                    //请求失败
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(reconciliation));
                    log.info("=================【NextPos退款】=================【退款操作 上报队列 MQ_QJS_TZSB_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
                    //rabbitMQSender.send(AD3MQConstant.MQ_QJS_TZSB_DL, JSON.toJSONString(rabbitMassage));
                    //TODO
                }

            }
        } else {
            //请求失败
            baseResponse.setMsg(EResultEnum.REFUNDING.getCode());
            orderRefund.setRemark3(JSON.toJSONString(orderRefund));
            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orderRefund));
            log.info("===============【NextPos退款】===============【退款操作 请求失败上报队列 MQ_TK_NEXTPOS_QQSB_DL】 rabbitMassage: {} ", JSON.toJSONString(rabbitMassage));
            //rabbitMQSender.send(AD3MQConstant.MQ_TK_NEXTPOS_QQSB_DL, JSON.toJSONString(rabbitMassage));
         //TODO
        }
        return response;
    }
}
