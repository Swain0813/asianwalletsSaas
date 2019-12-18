package com.asianwallets.trade.service;

import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.entity.Currency;
import com.asianwallets.common.entity.ExchangeRate;

/**
 * 通用获取数据接口
 */
public interface CommonRedisDataService {

    /**
     * 根据币种编码获取币种
     *
     * @param code 币种编码
     * @return 币种
     */
    Currency getCurrencyByCode(String code);

    /**
     * 根据商户ID获取密钥对象
     *
     * @param merchantId 商户ID
     * @return 密钥
     */
    Attestation getAttestationByMerchantId(String merchantId);

    /**
     * 根据本币与外币获取汇率
     *
     * @param localCurrency   本币
     * @param foreignCurrency 外币
     * @return 汇率
     */
    ExchangeRate getExchangeRateByCurrency(String localCurrency, String foreignCurrency);
}
