package com.asianwallets.trade.service.impl;

import com.asianwallets.trade.service.CommonRedisDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通用获取数据接口
 */
@Service
@Slf4j
public class CommonRedisDataServiceImpl implements CommonRedisDataService {

    /**
     * 根据币种获取币种默认值
     *
     * @param currency 币种
     * @return 默认值
     */
    @Override
    public String getCurrencyDefaultValue(String currency) {
        return null;
    }
}
