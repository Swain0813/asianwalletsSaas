package com.asianwallets.trade.service;

import com.asianwallets.common.entity.Attestation;

/**
 * 通用获取数据接口
 */
public interface CommonRedisDataService {

    /**
     * 根据币种获取币种默认值
     *
     * @param currency 币种
     * @return 默认值
     */
    String getCurrencyDefaultValue(String currency);

    /**
     * 根据商户ID获取密钥对象
     *
     * @param merchantId 商户ID
     * @return 默认值
     */
    Attestation getAttestationByMerchantId(String merchantId);
}
