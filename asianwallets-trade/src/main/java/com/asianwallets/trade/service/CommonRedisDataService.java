package com.asianwallets.trade.service;

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
}
