package com.asianwallets.channels.service.impl;
import cn.hutool.http.Header;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.Help2PayService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.help2pay.Help2PayOutDTO;
import com.asianwallets.common.dto.help2pay.Help2PayRequestDTO;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.MD5;
import com.asianwallets.common.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-06-10 10:49
 **/
@Service
@Slf4j
@Transactional
public class Help2PayServiceImpl implements Help2PayService {

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Override
    public BaseResponse help2Pay(Help2PayRequestDTO help2PayRequestDTO) {
        log.info("===============【Help2Pay收单接口】===============【请求参数】 help2PayRequestDTO:{}", JSON.toJSONString(help2PayRequestDTO));
        BaseResponse response = new BaseResponse();
        String body = null;
        try {
            int num = channelsOrderMapper.selectCountById(help2PayRequestDTO.getReference());
            ChannelsOrder co;
            if (num > 0) {
                co = channelsOrderMapper.selectByPrimaryKey(help2PayRequestDTO.getReference());
            } else {
                co = new ChannelsOrder();
            }
            co.setMerchantOrderId(help2PayRequestDTO.getInstitutionOrderId());
            co.setTradeCurrency(help2PayRequestDTO.getCurrency());
            co.setTradeAmount(new BigDecimal(help2PayRequestDTO.getAmount()));
            co.setReqIp(help2PayRequestDTO.getReqIp());
            co.setBrowserUrl(help2PayRequestDTO.getFrontURI());
            co.setServerUrl(help2PayRequestDTO.getBackURI());
            co.setTradeStatus(Byte.valueOf(TradeConstant.TRADE_WAIT));
            co.setIssuerId(help2PayRequestDTO.getBank());
            co.setMd5KeyStr(help2PayRequestDTO.getMd5KeyStr());
            co.setId(help2PayRequestDTO.getReference());
            co.setOrderType(Byte.valueOf(AD3Constant.TRADE_ORDER));
            if (num > 0) {
                co.setUpdateTime(new Date());
                channelsOrderMapper.updateByPrimaryKeySelective(co);
            } else {
                co.setCreateTime(new Date());
                channelsOrderMapper.insert(co);
            }
            //todo
            cn.hutool.http.HttpResponse execute = HttpRequest.post(help2PayRequestDTO.getChannel().getPayUrl())
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(BeanToMapUtil.beanToMap(help2PayRequestDTO))
                    .timeout(20000)
                    .execute();
            int status = execute.getStatus();
            body = execute.body();
            log.info("===============【Help2Pay收单接口】===============【收单响应参数】 http状态码: {} | body: {}", status, JSON.toJSONString(body));
            //判断HTTP状态码
            if (AsianWalletConstant.HTTP_SUCCESS_STATUS != status || StringUtils.isEmpty(body)) {
                response.setCode(TradeConstant.HTTP_FAIL);
                response.setMsg(TradeConstant.HTTP_FAIL_MSG);
                return response;
            }
        } catch (HttpException e) {
            log.info("===============【Help2Pay收单接口】===============【接口异常】", e);
            response.setCode(TradeConstant.HTTP_FAIL);
            response.setMsg(TradeConstant.HTTP_FAIL_MSG);
            return response;
        }
        response.setCode(TradeConstant.HTTP_SUCCESS);
        response.setMsg(TradeConstant.HTTP_SUCCESS_MSG);
        response.setData(body);
        return response;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/17
     * @Descripate HELP2PAY汇款接口
     **/
    @Override
    public BaseResponse help2PayOut(Help2PayOutDTO help2PayOutDTO) {
        //请求参数
        BaseResponse response = new BaseResponse();
        try {
            // 这里将查询的url作为IP地址
            help2PayOutDTO.setClientIP(help2PayOutDTO.getChannel().getChannelSingleSelectUrl());
            help2PayOutDTO.setKey(createDepositRequestKey(help2PayOutDTO));
            log.info("----------------- HELP2PAY汇款接口 ----------------- help2PayOutDTO:{}", JSON.toJSONString(help2PayOutDTO));
            long start = System.currentTimeMillis();
            //todo
            log.info("------- HELP2PAY汇款接口消耗时间 -------url:{} ", help2PayOutDTO.getChannel().getPayUrl());
            cn.hutool.http.HttpResponse execute = HttpRequest.post(help2PayOutDTO.getChannel().getPayUrl())
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .form(BeanToMapUtil.beanToMap(help2PayOutDTO))
                    .timeout(200000)
                    .execute();
            long end = System.currentTimeMillis();
            log.info("------- HELP2PAY汇款接口消耗时间 -------Time:{} MS", (end - start));
            int status = execute.getStatus();
            String body = execute.body();
            log.info("----------------------向上游接口发送订单返回日志记录----------------------http状态码:{},body:{}", status, JSON.toJSON(body));
            //判断HTTP状态码
            if (status != AsianWalletConstant.HTTP_SUCCESS_STATUS) {
                response.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
                return response;
            }
            if (StringUtils.isEmpty(body)) {
                log.info("----------------------HELP2PAY汇款接口失败日志记录 ----------------------help2PayOutDTO:{}", JSON.toJSON(help2PayOutDTO));
                response.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
                return response;
            }
            //response.setData(body);
            Map<String, String> map = XMLUtil.xmlToMap(body, "UTF-8");
            log.info("----------------------HELP2PAY汇款接口失败日志记录 ----------------------map:{}", JSON.toJSON(map));
            if (map.get("statusCode").equals("000")) {
                response.setCode("200");
            } else {
                response.setCode(EResultEnum.ORDER_CREATION_FAILED.getCode());
                response.setMsg(map.get("message"));
            }
        } catch (Exception e) {
            log.info("----------------------HELP2PAY汇款接口失败日志记录 ----------------------Exception:{}", e);
        }
        return response;

    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/17
     * @Descripate 汇款接口生成签名
     **/
    public String createDepositRequestKey(Help2PayOutDTO help2PayOutDTO) {
        log.info("------------------- help2Pay 汇款接口生成签名  ----------------------- help2PayOutDTO：{}", JSON.toJSONString(help2PayOutDTO));
        String key = null;//生成签名后的key
        String origin = "";//签名前的明文字符串
        if (help2PayOutDTO != null) {
            /*
             *MD5({MerchantCode }{TransactionId }{MemberCode }{Amount}{CurrencyCode}{TransactionDatetime}{ToBankAccountNumber }{SecurityCode})
             */
            if (!StringUtils.isEmpty(help2PayOutDTO.getMemberCode())) {
                origin = origin + help2PayOutDTO.getMemberCode();
            }
            if (!StringUtils.isEmpty(help2PayOutDTO.getTransactionID())) {
                origin = origin + help2PayOutDTO.getTransactionID();
            }
            if (!StringUtils.isEmpty(help2PayOutDTO.getMemberCode())) {
                origin = origin + help2PayOutDTO.getMemberCode();
            }
            if (!StringUtils.isEmpty(help2PayOutDTO.getAmount())) {
                origin = origin + help2PayOutDTO.getAmount();
            }
            if (!StringUtils.isEmpty(help2PayOutDTO.getCurrencyCode())) {
                origin = origin + help2PayOutDTO.getCurrencyCode();
            }
            if (!StringUtils.isEmpty(help2PayOutDTO.getTransactionDateTime())) {
                origin = origin + help2PayOutDTO.getTransactionDateTime();
            }
            if (!StringUtils.isEmpty(help2PayOutDTO.getToBankAccountNumber())) {
                origin = origin + help2PayOutDTO.getToBankAccountNumber();
            }
            if (!StringUtils.isEmpty(help2PayOutDTO.getSecurityCode())) {
                origin = origin + help2PayOutDTO.getSecurityCode();
            }
            if (origin != null && !origin.equals("")) {
                origin = origin.trim();
            }
            log.info("------------------- help2Pay 汇款接口生成签名  ----------------------- origin：{}", JSON.toJSONString(origin));
            key = MD5.MD5Encode(origin).toUpperCase();
            log.info("------------------- help2Pay 汇款接口生成签名  ----------------------- key：{}", JSON.toJSONString(key));
        }
        return key;
    }

}
