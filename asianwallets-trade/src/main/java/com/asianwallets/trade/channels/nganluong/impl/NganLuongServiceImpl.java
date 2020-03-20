package com.asianwallets.trade.channels.nganluong.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.nganluong.NganLuongDTO;
import com.asianwallets.common.dto.nganluong.NganLuongMQDTO;
import com.asianwallets.common.dto.nganluong.NganLuongRequestDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.RabbitMassage;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.trade.channels.nganluong.NganLuongService;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.utils.HandlerType;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@Transactional
@HandlerType(TradeConstant.NGANLUONG)
public class NganLuongServiceImpl implements NganLuongService {

    @Autowired
    @Qualifier(value = "ad3ParamsConfig")
    private AD3ParamsConfig ad3ParamsConfig;

    @ApiModelProperty("支付页面")
    @Value("${custom.paySuccessUrl}")
    private String paySuccessUrl;//签名方式

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * NL网银收单接口信息记录
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    @Override
    public BaseResponse nganLuongPay(Orders orders, Channel channel, BaseResponse baseResponse) {
        NganLuongRequestDTO nganLuongRequestDTO = new NganLuongRequestDTO(channel, orders, ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_SUCCESS, paySuccessUrl + "?page=" + TradeConstant.PAGE_PROCESSING);
        NganLuongDTO nganLuongDTO = new NganLuongDTO(nganLuongRequestDTO, orders.getMerchantOrderId(), orders.getReqIp(), channel);
        log.info("===============【NL网银收单接口信息记录】===============【请求实体】 nganLuongDTO: {}", JSON.toJSONString(nganLuongDTO));
        BaseResponse response = channelsFeign.nganLuongPay(nganLuongDTO);
        //判断baseResponse code
        if (!StringUtils.isEmpty(response.getCode())) {
            baseResponse.setCode(response.getCode());
            baseResponse.setData(response.getData());
            return baseResponse;
        }
        Map<String, String> map = (Map<String, String>) response.getData();
        //判断error_code
        String error_code = map.get("error_code");
        if (!error_code.equals("00")) {
            baseResponse.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
            //上游返回的错误code
            orders.setRemark4(error_code.concat(" ").concat(map.get("description")));
            //更新订单信息
            try {
                ordersMapper.updateByPrimaryKeySelective(orders);
            } catch (Exception e) {
                log.error("===============【NL网银收单接口信息记录】===============【更新订单信息异常】");
            }
            return baseResponse;
        }
        //将查询token放入订单
        orders.setSign(map.get("token"));
        ordersMapper.updateByPrimaryKeySelective(orders);
        NganLuongMQDTO nganLuongMQDTO = new NganLuongMQDTO(map.get("token"), orders.getId(), channel.getChannelMerchantId(), channel.getMd5KeyStr());
        String jsonNLMQDTO = JSON.toJSONString(nganLuongMQDTO);
        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, jsonNLMQDTO);
        log.info("===============【NL网银收单接口信息记录】===============【上报NL查询订单队列1】 E_MQ_NGANLUONG_CHECK_ORDER_DL=======  jsonNLMQDTO: {}", jsonNLMQDTO);
        rabbitMQSender.send(AD3MQConstant.E_MQ_NGANLUONG_CHECK_ORDER_DL, JSON.toJSONString(rabbitMassage));
        baseResponse.setData(response.getData());
        log.info("===============【NL网银收单接口信息记录】===============【响应实体】 baseResponse: {}", JSON.toJSONString(baseResponse));
        if (StringUtils.isEmpty(baseResponse.getCode())) {
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        OnlineTradeVO onlineTradeVO = new OnlineTradeVO();
        Map<String, String> infoMap = (Map<String, String>) baseResponse.getData();
        //网银
        onlineTradeVO.setRespCode("T000");
        onlineTradeVO.setType(TradeConstant.ONLINE_BANKING);
        onlineTradeVO.setCode_url(TradeConstant.START + infoMap.get("checkout_url") + TradeConstant.END);//跳转URL
        baseResponse.setData(onlineTradeVO);
        return baseResponse;
    }

}
