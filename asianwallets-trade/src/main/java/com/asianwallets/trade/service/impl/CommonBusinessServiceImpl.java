package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.entity.Currency;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.MD5Util;
import com.asianwallets.common.utils.RSAUtils;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.utils.SignTools;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Map;


/**
 * 通用业务接口
 */
@Slf4j
@Service
public class CommonBusinessServiceImpl implements CommonBusinessService {

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private RedisService redisService;

    /**
     * 校验MD5签名
     *
     * @param obj 验签对象
     * @return 布尔值
     */
    @Override
    public boolean checkSignByMd5(Object obj) {
        //将对象转换成Map
        Map<String, String> map = ReflexClazzUtils.getFieldForStringValue(obj);
        Attestation attestation = commonRedisDataService.getAttestationByMerchantId(map.get("merchantId"));
        if (attestation == null) {
            return false;
        }
        //将请求参数排序后与md5Key拼接
        String clearText = SignTools.getSignStr(map) + attestation.getMd5key();
        log.info("===============【校验MD5签名】===============【签名前的明文】 clearText: {}", clearText);
        String decryptSign = MD5Util.getMD5String(clearText);
        log.info("===============【校验MD5签名】===============【签名后的密文】 decryptSign: {}", decryptSign);
        return map.get("sign").equalsIgnoreCase(decryptSign);
    }

    /**
     * 校验线上签名
     *
     * @param o
     * @return
     */
    @Override
    public boolean checkOnlineSignMsg(Object o) {
        Map<String, String> map = ReflexClazzUtils.getFieldForStringValue(o);
        String sign = String.valueOf(map.get("sign"));
        if (sign == null || "".equals(sign)) {
            throw new BusinessException(EResultEnum.SIGNATURE_CANNOT_BE_EMPTY.getCode());
        }
        if (map.get("serialVersionUID") != null) {
            map.put("serialVersionUID", null);
        }
        if (map.get("reqIp") != null) {
            map.put("reqIp", null);
        }
        if (map.get("sign") != null) {
            map.put("sign", null);
        }
        if (map.get("sort") != null) {
            map.put("sort", null);
        }
        if (map.get("order") != null) {
            map.put("order", null);
        }
        Attestation attestation = commonRedisDataService.getAttestationByMerchantId(String.valueOf(map.get("merchantId")));
        if (attestation == null) {
            return false;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] signMsg = decoder.decode(sign);
        map.put("sign", null);
        byte[] data = SignTools.getSignStr(map).getBytes();
        try {
            return RSAUtils.verify(data, signMsg, attestation.getPubkey());
        } catch (Exception e) {
            log.info("----------- 签名校验发生错误----------merchantId:{},签名signMsg:{}", String.valueOf(map.get("merchantId")), signMsg);
        }
        return false;
    }


    /**
     * 校验重复请求【线上与线下下单】
     *
     * @param merchantId      商户编号
     * @param merchantOrderId 商户订单号
     * @return 布尔值
     */
    @Override
    public boolean repeatedRequests(String merchantId, String merchantOrderId) {
        //拼接重复请求KEY
        String redisKey = TradeConstant.REPEATED_REQUEST.concat(merchantId.concat("_").concat(merchantOrderId));
        if (!StringUtils.isEmpty(redisService.get(redisKey)) && redisService.get(redisKey).equals(merchantOrderId)) {
            return false;
        }
        redisService.set(redisKey, merchantOrderId, 2);
        return true;
    }

    /**
     * 校验订单币种是否支持与默认值【线上与线下下单】
     *
     * @param orderCurrency 订单币种
     * @param orderAmount   订单金额
     * @return 布尔值
     */
    @Override
    public boolean checkOrderCurrency(String orderCurrency, BigDecimal orderAmount) {
        //获取币种默认值
        Currency currency = commonRedisDataService.getCurrencyByCode(orderCurrency);
        if (currency == null) {
            throw new BusinessException(EResultEnum.PRODUCT_CURRENCY_NO_SUPPORT.getCode());
        }
        return new StringBuilder(currency.getDefaults()).reverse().indexOf(".") >= new StringBuilder(String.valueOf(orderAmount)).reverse().indexOf(".");
    }
}
