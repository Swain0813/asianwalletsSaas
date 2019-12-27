package com.asianwallets.trade.channels.megaPay.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.megapay.MegaPayIDRRequestDTO;
import com.asianwallets.common.dto.megapay.MegaPayRequestDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.megaPay.MegaPayService;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@Transactional
@HandlerType(TradeConstant.MEGAPAY)
public class MegaPayServiceImpl extends ChannelsAbstractAdapter implements MegaPayService {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private ChannelsFeign channelsFeign;

    /**
     * MegaPay网银收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        BaseResponse response = new BaseResponse();
        if (orders.getTradeCurrency().equalsIgnoreCase("THB")) {//megaPay THB通道
            MegaPayRequestDTO megaPayRequestDTO = new MegaPayRequestDTO(orders, channel);
            log.info("===============【MegaPay-THB网银收单】===============【调用Channels服务-请求参数】 megaPayRequestDTO: {}", JSON.toJSONString(megaPayRequestDTO));
            response = channelsFeign.megaPayTHB(megaPayRequestDTO);
            log.info("===============【MegaPay-THB网银收单】===============【调用Channels服务-响应参数】 response: {}", JSON.toJSONString(response));
            //状态码不为200的时候
            if (!TradeConstant.HTTP_SUCCESS.equals(response.getCode())) {
                log.info("==============【MegaPay-THB网银收单】==============调用Channels服务【Help2Pay接口】-状态码异常 code: {}", response.getCode());
                throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
            }
            JSONObject param = new JSONObject();
            param.put("orderId", orders.getId());
            param.put("channelMerchantId", channel.getChannelMerchantId());
            log.info("===============【MegaPay-THB网银收单】===============【上报MegaPay-THB查询订单队列1】");
            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(param));
            rabbitMQSender.send(AD3MQConstant.E_MQ_MEGAPAY_THB_CHECK_ORDER, JSON.toJSONString(rabbitMassage));
        } else if (orders.getTradeCurrency().equalsIgnoreCase("IDR")) {//megaPay IDR通道
            MegaPayIDRRequestDTO megaPayIDRRequestDTO = new MegaPayIDRRequestDTO(orders, channel);
            log.info("===============【MegaPay-IDR网银收单】===============【调用Channels服务-请求参数】 megaPayIDRRequestDTO: {}", JSON.toJSONString(megaPayIDRRequestDTO));
            response = channelsFeign.megaPayIDR(megaPayIDRRequestDTO);
            log.info("===============【MegaPay-IDR网银收单】===============【调用Channels服务-响应参数】 response: {}", JSON.toJSONString(response));
            //状态码不为200的时候
            if (!TradeConstant.HTTP_SUCCESS.equals(response.getCode())) {
                log.info("==============【MegaPay-IDR网银收单】==============用Channels服务【Help2Pay接口】-状态码异常 code: {}", response.getCode());
                throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
            }
        }
        if (StringUtils.isEmpty(response.getData())) {
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        String megaInfo = (String) response.getData();
        OnlineTradeVO onlineTradeVO = new OnlineTradeVO();
        if (megaInfo.replaceAll("\\s*", "").matches(".*html.*")) {
            //网银
            onlineTradeVO.setRespCode("T000");
            if (megaInfo.contains("href=\"")) {
                onlineTradeVO.setCode_url(TradeConstant.START + megaInfo.substring(megaInfo.indexOf("href=\"") + 6, megaInfo.indexOf("\">here</a>")) + TradeConstant.END);
            } else {
                onlineTradeVO.setCode_url(megaInfo);
            }
            onlineTradeVO.setType(TradeConstant.ONLINE_BANKING);
            response.setData(onlineTradeVO);
            return response;
        }
        return response;
    }
}
