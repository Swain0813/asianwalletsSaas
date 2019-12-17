package com.asianwallets.trade.service.impl;

import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.MD5Util;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.utils.SignTools;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
     * 校验订单金额是否符合币种默认值【线上与线下下单】
     *
     * @param orderCurrency 订单币种
     * @param orderAmount   订单金额
     * @return 布尔值
     */
    @Override
    public boolean checkOrderCurrency(String orderCurrency, BigDecimal orderAmount) {
        //获取币种默认值
        String defaultValue = commonRedisDataService.getCurrencyDefaultValue(orderCurrency);
        if (StringUtils.isEmpty(defaultValue)) {
            throw new BusinessException(EResultEnum.PRODUCT_CURRENCY_NO_SUPPORT.getCode());
        }
        String orderAmountStr = String.valueOf(orderAmount);
        return new StringBuilder(defaultValue).reverse().indexOf(".") >= new StringBuilder(orderAmountStr).reverse().indexOf(".");
    }
}
