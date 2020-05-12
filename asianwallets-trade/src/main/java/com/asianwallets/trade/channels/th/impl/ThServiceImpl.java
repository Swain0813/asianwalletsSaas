package com.asianwallets.trade.channels.th.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.dto.th.ISO8583.ISO8583Util;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.th.ThService;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
@HandlerType(TradeConstant.TH)
public class ThServiceImpl extends ChannelsAbstractAdapter implements ThService {

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    /**
     * 通华主扫接口
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        ChannelsOrder channelsOrder = new ChannelsOrder();
        channelsOrder.setId(orders.getId());
        channelsOrder.setMerchantOrderId(orders.getMerchantOrderId());
        channelsOrder.setTradeCurrency(orders.getTradeCurrency());
        channelsOrder.setTradeAmount(orders.getTradeAmount());
        channelsOrder.setReqIp(orders.getReqIp());
        channelsOrder.setServerUrl(orders.getServerUrl());
        channelsOrder.setTradeStatus(TradeConstant.TRADE_WAIT);
        channelsOrder.setIssuerId(orders.getIssuerId());
        channelsOrder.setOrderType(AD3Constant.TRADE_ORDER);
        channelsOrder.setMd5KeyStr(channel.getMd5KeyStr());
        channelsOrder.setPayerPhone(orders.getPayerPhone());
        channelsOrder.setPayerName(orders.getPayerName());
        channelsOrder.setPayerBank(orders.getPayerBank());
        channelsOrder.setPayerEmail(orders.getPayerEmail());
        channelsOrder.setCreateTime(new Date());
        channelsOrder.setCreator(orders.getCreator());
        channelsOrderMapper.insert(channelsOrder);

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        //交易处理码
        iso8583DTO.setProcessingCode_3("000000");
        String tradeAmountStr = String.valueOf(orders.getTradeAmount());
        int tradeAmount = 0;
        if (new BigDecimal(orders.getTradeAmount().intValue()).compareTo(orders.getTradeAmount()) == 0) {
            //整数
            tradeAmount = orders.getTradeAmount().intValue();
        } else {
            //小数位数
            int numOfBits = tradeAmountStr.length() - tradeAmountStr.indexOf(".") - 1;
            //小数,扩大对应小数位数
            tradeAmount = orders.getTradeAmount().movePointRight(numOfBits).intValue();
        }
        //12位,左边填充0
        String formatAmount = String.format("%012d", tradeAmount);
        //交易金额
        iso8583DTO.setAmountOfTransactions_4(formatAmount);
        //iso8583DTO.setAmountOfTips_5(""); TODO
        //受卡方系统跟踪号 TODO
        iso8583DTO.setSystemTraceAuditNumber_11("");
        //受卡方所在地时间HHmmss
        iso8583DTO.setTimeOfLocalTransaction_12(DateToolUtils.getReqTimeHHmmss());
        //受卡方所在地日期MMdd
        iso8583DTO.setDateOfLocalTransaction_13(DateToolUtils.getReqTimeMMdd());
        //服务点输入方式码 TODO
        iso8583DTO.setPointOfServiceEntryMode_22("021");
        //服务点条件码 TODO
        iso8583DTO.setPointOfServiceConditionMode_25("00");
        //受理方标识码 (机构号)
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("08600005");
        //iso8583DTO.setRetrievalReferenceNumber_37("");
        //iso8583DTO.setResponseCode_39("");
        //受卡机终端标识码 (设备号)
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00018644");
        //受卡方标识码 (商户号)
        iso8583DTO.setCardAcceptorIdentificationCode_42(channel.getChannelMerchantId());
        //iso8583DTO.setAdditionalData_46("");
        //iso8583DTO.setAdditionalDataPrivate_47("");
        //交易货币代码
        iso8583DTO.setCurrencyCodeOfTransaction_49("156");
        //自定义域
        iso8583DTO.setReservedPrivate_60("");
        //iso8583DTO.setReservedPrivate_63("");
        //报文鉴别码
        iso8583DTO.setMessageAuthenticationCode_64("");
        log.info("==================【通华线下CSB】==================【调用Channels服务】【请求参数】 iso8583DTO: {}", JSON.toJSONString(iso8583DTO));
        BaseResponse channelResponse = channelsFeign.thCSB(iso8583DTO);
        log.info("==================【通华线下CSB】==================【调用Channels服务】【通华-CSB接口】  channelResponse: {}", JSON.toJSONString(channelResponse));
        if (!TradeConstant.HTTP_SUCCESS.equals(channelResponse.getCode())) {
            log.info("==================【通华线下CSB】==================【调用Channels服务】【通华-CSB接口】-【请求状态码异常】");
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        return baseResponse;
    }

    public static void main(String[] args) throws Exception {
        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("6006090000800100000000852999958120501000186440000000086000050000000085299995812050100000059");
        iso8583DTO.setSystemTraceAuditNumber_11("198124");
        iso8583DTO.setAcquiringInstitutionIdentificationCode_32("52000001");
        iso8583DTO.setCardAcceptorTerminalIdentification_41("00002753");
        iso8583DTO.setCardAcceptorIdentificationCode_42("852999958120050");
        iso8583DTO.setReservedPrivate_60("50000001003");
        iso8583DTO.setReservedPrivate_63("001");
        String sendMsg = ISO8583Util.packISO8583DTO(iso8583DTO);
        System.out.println("发送报文: " + sendMsg);
        String receiveMsg = ISO8583Util.send8583("0086600609000080010000000038353239393939353831323030353030303030323735333330303030303030303030303030333030303030303031383532393939393538313230303530303030303030353408000020000100C000121981240852000001303030303237353338353239393939353831323030353000115000000100300003303031",
                "58.248.241.169", 10089);
        System.out.println("接收报文: " + receiveMsg);
        //ISO8583DTO iso8583DTO1 = ISO8583Util.unpackISO8583DTO(s1);
        //System.out.println(JSON.toJSON(iso8583DTO1));
    }

}