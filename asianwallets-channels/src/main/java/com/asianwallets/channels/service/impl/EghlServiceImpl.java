package com.asianwallets.channels.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.config.ChannelsConfig;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.EghlService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.eghl.EGHLRequestDTO;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.BeanToMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: EGHL
 * @author: YangXu
 * @create: 2019-05-28 14:15
 **/
@Service
@Slf4j
public class EghlServiceImpl implements EghlService {

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private ChannelsConfig channelsConfig;


    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate eghl收单接口
     **/
    @Override
    public BaseResponse eGHLPay(EGHLRequestDTO eghlRequestDTO) {
        int num = channelsOrderMapper.selectCountById(eghlRequestDTO.getPaymentID());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(eghlRequestDTO.getPaymentID());
        } else {
            co = new ChannelsOrder();
        }
        co.setMerchantOrderId(eghlRequestDTO.getOrderNumber());
        co.setTradeCurrency(eghlRequestDTO.getCurrencyCode());
        co.setTradeAmount(new BigDecimal(eghlRequestDTO.getAmount()));
        co.setReqIp(eghlRequestDTO.getCustIP());
        co.setPayerName(eghlRequestDTO.getCustName());
        co.setPayerEmail(eghlRequestDTO.getCustEmail());
        co.setBrowserUrl(eghlRequestDTO.getMerchantReturnURL());
        co.setServerUrl(eghlRequestDTO.getMerchantCallBackURL());
        co.setPayerPhone(eghlRequestDTO.getCustPhone());
        co.setTradeStatus(Byte.valueOf(TradeConstant.TRADE_WAIT));
        co.setIssuerId(eghlRequestDTO.getIssuingBank());
        co.setMd5KeyStr(eghlRequestDTO.getMd5KeyStr());
        co.setId(eghlRequestDTO.getPaymentID());
        co.setOrderType(Byte.valueOf(AD3Constant.TRADE_ORDER));
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }

        BaseResponse response = new BaseResponse();
        log.info("-----------------eghl收单接口----------------- eghlRequestDTO:{}", JSON.toJSONString(eghlRequestDTO));
        long start = System.currentTimeMillis();
        cn.hutool.http.HttpResponse execute = HttpRequest.post(channelsConfig.getPayUrl())
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(BeanToMapUtil.beanToMap(eghlRequestDTO))
                .timeout(20000)
                .execute();
        long end = System.currentTimeMillis();
        log.info("-------eghl通道消耗时间-------Time:{} MS", (end - start));
        int status = execute.getStatus();
        String body = execute.body();
        log.info("----------------------向上游接口发送订单返回日志记录----------------------http状态码:{},body:{}", status, JSON.toJSON(body));
        //判断HTTP状态码
        if (status != AsianWalletConstant.HTTP_SUCCESS_STATUS) {
            response.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
            return response;
        }
        if (StringUtils.isEmpty(body)) {
            response.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
            return response;
        }
        response.setData(body);
        return response;


    }
}
