package com.asianwallets.channels.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.NganLuongService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.nganluong.NganLuongDTO;
import com.asianwallets.common.dto.nganluong.NganLuongQueryDTO;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @description: NganLuong通道
 * @author: YangXu
 * @create: 2019-06-18 11:13
 **/
@Service
@Slf4j
public class NganLuongServiceImpl implements NganLuongService {

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate NganLuong收单接口
     **/
    @Override
    public BaseResponse nganLuongPay(NganLuongDTO nganLuongDTO) {
        int num = channelsOrderMapper.selectCountById(nganLuongDTO.getNganLuongRequestDTO().getOrder_code());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(nganLuongDTO.getNganLuongRequestDTO().getOrder_code());
        } else {
            co = new ChannelsOrder();
        }
        co.setInstitutionOrderId(nganLuongDTO.getInstitutionOrderId());
        co.setTradeCurrency("VND");
        co.setTradeAmount(new BigDecimal(nganLuongDTO.getNganLuongRequestDTO().getTotal_amount()));
        co.setReqIp(nganLuongDTO.getReqIp());
        co.setDraweeName(nganLuongDTO.getNganLuongRequestDTO().getBuyer_fullname());
        co.setDraweeEmail(nganLuongDTO.getNganLuongRequestDTO().getBuyer_email());
        co.setBrowserUrl(nganLuongDTO.getNganLuongRequestDTO().getReturn_url());
        //co.setServerUrl("");
        co.setDraweePhone(nganLuongDTO.getNganLuongRequestDTO().getBuyer_mobile());
        co.setTradeStatus(TradeConstant.TRADE_WAIT);
        co.setIssuerId(nganLuongDTO.getNganLuongRequestDTO().getBank_code());
        co.setMd5KeyStr(nganLuongDTO.getNganLuongRequestDTO().getMerchant_password());
        co.setId(nganLuongDTO.getNganLuongRequestDTO().getOrder_code());
        co.setOrderType(AD3Constant.TRADE_ORDER.toString());
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }

        BaseResponse response = new BaseResponse();
        log.info("-----------------NganLuong收单接口----------------- nganLuongDTO:{} getNganLuongPayUrl:{}", JSON.toJSONString(nganLuongDTO), nganLuongDTO.getChannel().getPayUrl());
        long start = System.currentTimeMillis();
        cn.hutool.http.HttpResponse execute = HttpRequest.post(nganLuongDTO.getChannel().getPayUrl())
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(BeanToMapUtil.beanToMap(nganLuongDTO.getNganLuongRequestDTO()))
                .timeout(20000)
                .execute();
        long end = System.currentTimeMillis();
        log.info("-------NganLuong收单接口-------Time:{} MS", (end - start));
        int status = execute.getStatus();
        String body = execute.body();
        log.info("----------------------NganLuong收单接口返回----------------------http状态码:{},body:{}", status, JSON.toJSON(body));
        //判断HTTP状态码
        if (status != AsianWalletConstant.HTTP_SUCCESS_STATUS) {
            response.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
            return response;
        }
        // 注解方式xml转换为map对象
        if (StringUtils.isEmpty(body)) {
            response.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
            return response;
        }
        try {
            Map<String, String> map = XMLUtil.xmlToMap(body, "UTF-8");
            log.info("----------------------NganLuong收单接口----------------------转换后的 map:{}", map);
            response.setData(map);
        } catch (Exception e) {
            log.info("----------------------NganLuong收单接口 xml 转换异常 ----------------------body:{}", body);
        }
        return response;
    }

    /**
     * @param nganLuongQueryDTO 查询实体
     * @return BaseResponse
     * @Descripate NganLuong查询接口
     **/
    @Override
    public BaseResponse nganLuongQuery(NganLuongQueryDTO nganLuongQueryDTO) {
        BaseResponse baseResponse = new BaseResponse();
        log.info("=============== 【NL查询接口】===============【查询参数记录】 nganLuongQueryDTO:{}", JSON.toJSONString(nganLuongQueryDTO));
        cn.hutool.http.HttpResponse execute = HttpRequest.post(nganLuongQueryDTO.getChannel().getChannelSingleSelectUrl())
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(BeanToMapUtil.beanToMap(nganLuongQueryDTO))
                .timeout(30000)
                .execute();
        int status = execute.getStatus();
        String body = execute.body();
        log.info("=============== 【NL查询接口】=============== http状态码:{} , body:{}", status, JSON.toJSONString(body));
        //判断HTTP状态码
        if (status != AsianWalletConstant.HTTP_SUCCESS_STATUS) {
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            return baseResponse;
        }
        // 注解方式xml转换为map对象
        if (StringUtils.isEmpty(body)) {
            log.info("=============== 【NL查询接口】===============调用查询接口返回body为空");
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
            return baseResponse;
        }
        Map<String, String> resultMap = null;
        try {
            resultMap = XMLUtil.xmlToMap(body, "UTF-8");
            log.info("=============== 【NL查询接口】===============【解析后的XML结果】 resultMap:{}", JSON.toJSONString(resultMap));
        } catch (Exception e) {
            log.error("=============== 【NL查询接口】===============【xml转换异常】", e);
            baseResponse.setMsg(TradeConstant.HTTP_FAIL_MSG);
            baseResponse.setCode(TradeConstant.HTTP_FAIL);
        }
        baseResponse.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        baseResponse.setCode(TradeConstant.HTTP_SUCCESS);
        baseResponse.setData(resultMap);
        return baseResponse;
    }
}
