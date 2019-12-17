package com.asianwallets.trade.service.impl;

import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.redis.RedisService;
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

    /**
     * 根据币种获取币种默认值
     *
     * @param currency 币种
     * @return 默认值
     */
    @Override
    public String getCurrencyDefaultValue(String currency) {
        //当前币种的默认值
        String defaultValue = redisService.get(AsianWalletConstant.CURRENCY_DEFAULT + "_" + currency);
        try {
            if (StringUtils.isEmpty(defaultValue)) {
                defaultValue = currencyMapper.selectByCurrency(currency);
                if (StringUtils.isEmpty(defaultValue)) {
                    log.info("==================【根据币种获取币种默认值】==================【币种默认值不存在】 currency: {}", currency);
                    return defaultValue;
                }
                redisService.set(AsianWalletConstant.CURRENCY_DEFAULT + "_" + currency, defaultValue);
            }
        } catch (Exception e) {
            log.info("==================【根据币种获取币种默认值】==================【获取异常】", e);
        }
        return defaultValue;
    }
}
