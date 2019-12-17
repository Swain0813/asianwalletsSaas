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
//        String defaultValue = redisService.get(AsianWalletConstant.CURRENCY_DEFAULT + "_" + currency);
//        try {
//            if (StringUtils.isEmpty(defaultValue)) {
//                defaultValue = dictionaryMapper.selectByCurrency(currency);
//                if (StringUtils.isEmpty(defaultValue)) {
//                    //币种默认值不存在
//                    log.info("==================【币种默认值】获取失败================== tradeCurrency:{}", currency);
//                    return defaultValue;
//                }
//                redisService.set(AsianWalletConstant.CURRENCY_DEFAULT + "_" + currency, defaultValue);
//            }
//        } catch (Exception e) {
//            log.error("同步币种默认值到redis里发生异常:", e.getMessage());
//        }
//        log.info("================== CommonService getCurrencyDefaultValue =================== defaultValue: {}", defaultValue);
        return null;
    }
}
