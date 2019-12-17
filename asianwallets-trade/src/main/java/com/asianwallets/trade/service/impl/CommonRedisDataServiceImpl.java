package com.asianwallets.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.trade.dao.AttestationMapper;
import com.asianwallets.trade.dao.CurrencyMapper;
import com.asianwallets.trade.service.CommonRedisDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 通用获取数据接口
 */
@Service
@Slf4j
public class CommonRedisDataServiceImpl implements CommonRedisDataService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private AttestationMapper attestationMapper;

    /**
     * 根据币种获取币种默认值
     *
     * @param currency 币种
     * @return 默认值
     */
    @Override
    public String getCurrencyDefaultValue(String currency) {
        //当前币种的默认值
        String defaultValue = null;
        try {
            defaultValue = redisService.get(AsianWalletConstant.CURRENCY_DEFAULT + "_" + currency);
            if (StringUtils.isEmpty(defaultValue)) {
                defaultValue = currencyMapper.selectByCurrency(currency);
                if (StringUtils.isEmpty(defaultValue)) {
                    log.info("==================【根据币种获取币种默认值】==================【币种默认值不存在】 currency: {}", currency);
                    return null;
                }
                redisService.set(AsianWalletConstant.CURRENCY_DEFAULT + "_" + currency, defaultValue);
            }
        } catch (Exception e) {
            log.info("==================【根据币种获取币种默认值】==================【获取异常】", e);
        }
        return defaultValue;
    }

    /**
     * 根据商户ID获取密钥对象
     *
     * @param merchantId 商户ID
     * @return 默认值
     */
    @Override
    public Attestation getAttestationByMerchantId(String merchantId) {
        Attestation attestation = null;
        try {
            attestation = JSON.parseObject(redisService.get(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat(merchantId)), Attestation.class);
            if (attestation == null) {
                attestation = attestationMapper.selectByMerchantId(merchantId);
                if (attestation == null) {
                    log.info("==================【根据商户ID获取密钥对象】==================【密钥对象不存在】 merchantId: {}", merchantId);
                    return null;
                }
                redisService.set(AsianWalletConstant.ATTESTATION_CACHE_KEY.concat("_PF_").concat(merchantId), JSON.toJSONString(attestation));
            }
        } catch (Exception e) {
            log.info("==================【根据商户ID获取密钥对象】==================【获取异常】", e);
        }
        return attestation;
    }
}
