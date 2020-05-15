package com.asianwallets.trade.cache;

import com.asianwallets.common.entity.Country;
import com.asianwallets.trade.dao.CountryMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 所有国家的信息
 */
@Slf4j
@Service
public class CountryRedisService {

    @Autowired
    private CountryMapper countryMapper;

    /**
     * 国家信息本地缓存
     */
    private static Cache<String, Country> iCourierLists = CacheBuilder.newBuilder().initialCapacity(10).maximumSize(50).expireAfterWrite(30, TimeUnit.DAYS).build();

    /**
     * 国家信息本地缓存化
     *
     * @return
     */
    public Country getCountryLists(String id) {
        try {
            String key = id;
            return iCourierLists.get(key, () -> countryMapper.inquireCountry(id));
        } catch (Exception e) {
            log.error("国家信息本地缓存失败 信息不存在:" + e);
        }
        return null;
    }
}
