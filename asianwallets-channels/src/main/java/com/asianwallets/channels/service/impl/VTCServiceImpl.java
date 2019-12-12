package com.asianwallets.channels.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.asianwallets.channels.dao.ChannelsOrderMapper;
import com.asianwallets.channels.service.VTCService;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.vtc.VTCRequestDTO;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.ChannelsSignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-05-30 17:41
 **/
@Service
@Slf4j
public class VTCServiceImpl implements VTCService {

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    /**
     * vtcPay收单接口
     *
     * @param vtcRequestDTO
     * @return
     */
    @Override
    public BaseResponse vtcPay(VTCRequestDTO vtcRequestDTO) {
        int num = channelsOrderMapper.selectCountById(vtcRequestDTO.getReference_number());
        ChannelsOrder co;
        if (num > 0) {
            co = channelsOrderMapper.selectByPrimaryKey(vtcRequestDTO.getReference_number());
        } else {
            co = new ChannelsOrder();
        }
        co.setMerchantOrderId(vtcRequestDTO.getInstitutionOrderId());
        co.setTradeCurrency(vtcRequestDTO.getCurrency());
        co.setTradeAmount(new BigDecimal(vtcRequestDTO.getAmount()));
        co.setReqIp(vtcRequestDTO.getWebsite_id());
        co.setBrowserUrl(vtcRequestDTO.getUrl_return());
        co.setTradeStatus(Byte.valueOf(TradeConstant.TRADE_WAIT));
        co.setIssuerId(vtcRequestDTO.getPayment_type());
        co.setMd5KeyStr(vtcRequestDTO.getMd5KeyStr());
        co.setId(vtcRequestDTO.getReference_number());
        co.setReqIp(vtcRequestDTO.getReqIp());
        co.setOrderType(Byte.valueOf(AD3Constant.TRADE_ORDER));
        if (num > 0) {
            co.setUpdateTime(new Date());
            channelsOrderMapper.updateByPrimaryKeySelective(co);
        } else {
            co.setCreateTime(new Date());
            channelsOrderMapper.insert(co);
        }
        //签名
        vtcRequestDTO.setSignature(ChannelsSignUtils.getVtcSign(vtcRequestDTO, vtcRequestDTO.getMd5KeyStr()));
        BaseResponse response = new BaseResponse();
        log.info("-----------------VTC收单接口----------------- vtcRequestDTO:{}", JSON.toJSONString(vtcRequestDTO));
        long start = System.currentTimeMillis();
        cn.hutool.http.HttpResponse execute = HttpRequest.get(vtcRequestDTO.getChannel().getPayUrl())
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(BeanToMapUtil.beanToMap(vtcRequestDTO))
                .timeout(20000)
                .execute();
        log.info("-----------------VTC收单接口-----------------上报VTC url记录:{}", vtcRequestDTO.getChannel().getPayUrl());
        long end = System.currentTimeMillis();
        log.info("-------VTC通道消耗时间-------Time:{} MS", (end - start));
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

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/30
     * @Descripate 加签
     **/
//    public static String getSign(VTCRequestDTO vtcRequestDTO) {
//        StringBuffer sb = new StringBuffer();
//        if (!StringUtils.isEmpty(vtcRequestDTO.getAmount())) {
//            sb.append(vtcRequestDTO.getAmount().trim() + "|");
//        }
//        if (!StringUtils.isEmpty(vtcRequestDTO.getCurrency())) {
//            sb.append(vtcRequestDTO.getCurrency().trim() + "|");
//        }
//        if (!StringUtils.isEmpty(vtcRequestDTO.getPayment_type())) {
//            sb.append(vtcRequestDTO.getPayment_type().trim() + "|");
//        }
//        if (!StringUtils.isEmpty(vtcRequestDTO.getReceiver_account())) {
//            sb.append(vtcRequestDTO.getReceiver_account().trim() + "|");
//        }
//        if (!StringUtils.isEmpty(vtcRequestDTO.getReference_number())) {
//            sb.append(vtcRequestDTO.getReference_number().trim() + "|");
//        }
//        if (!StringUtils.isEmpty(vtcRequestDTO.getUrl_return())) {
//            sb.append(vtcRequestDTO.getUrl_return().trim() + "|");
//        }
//        if (!StringUtils.isEmpty(vtcRequestDTO.getWebsite_id())) {
//            sb.append(vtcRequestDTO.getWebsite_id().trim() + "|");
//        }
//        String sign = sb.toString() + vtcRequestDTO.getMd5KeyStr();
//        log.info("-----------------VTC收单接口-----------------加密前的明文:{}", sign);
//        return sign;
//    }


}
