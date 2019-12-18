package com.asianwallets.trade.service;

import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.entity.Currency;

/**
 * 通用获取数据接口
 */
public interface CommonRedisDataService {

    /**
     * 根据币种编码获取币种
     *
     * @param code 币种编码
     * @return 默认值
     */
    Currency getCurrencyByCode(String code);

    /**
     * 根据商户ID获取密钥对象
     *
     * @param merchantId 商户ID
     * @return 默认值
     */
    Attestation getAttestationByMerchantId(String merchantId);
}
