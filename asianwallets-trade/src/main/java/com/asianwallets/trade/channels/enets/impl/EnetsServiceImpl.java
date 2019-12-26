package com.asianwallets.trade.channels.enets.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.enets.EnetsOffLineRequestDTO;
import com.asianwallets.common.dto.enets.EnetsSMRequestDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.enets.EnetsService;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.EnetsPosCallbackDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
@Transactional
@HandlerType(TradeConstant.ENETS)
public class EnetsServiceImpl extends ChannelsAbstractAdapter implements EnetsService {

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private RabbitMQSender rabbitMQSender;


    /**
     * Enets线下CSB方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        EnetsSMRequestDTO enetsSMRequestDTO = new EnetsSMRequestDTO(orders, channel);
        EnetsOffLineRequestDTO enetsOffLineRequestDTO = new EnetsOffLineRequestDTO(enetsSMRequestDTO, orders, channel);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【Enets-CSB接口请求参数】 enetsOffLineRequestDTO: {}", JSON.toJSONString(enetsOffLineRequestDTO));
        BaseResponse channelResponse = channelsFeign.eNetsPosCSBPay(enetsOffLineRequestDTO);
        log.info("==================【线下CSB动态扫码】==================【调用Channels服务】【Enets-CSB接口响应参数】  channelResponse: {}", JSON.toJSONString(channelResponse));
        //请求失败
        if (channelResponse == null || !TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【线下CSB动态扫码】==================【Channels服务响应结果不正确】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(channelResponse.getData());
        return baseResponse;
    }

    /**
     * EnetsCSB回调
     *
     * @param enetsPosCallbackDTO eNetsCsb回调实体
     * @return
     */
    @Override
    public ResponseEntity<Void> eNetsCsbCallback(EnetsPosCallbackDTO enetsPosCallbackDTO, HttpServletResponse response) {
        return null;
    }
}
