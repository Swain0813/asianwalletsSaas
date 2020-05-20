package com.asianwallets.permissions.cache;

import com.asianwallets.common.entity.Country;
import com.asianwallets.permissions.dao.CountryMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
/**
 * 所有二位代码,三位代码以及数字代码的本地缓存
 */
public class CountryCodeRedisService {

    @Autowired
    private CountryMapper countryMapper;

    /**
     * 国家信息本地缓存
     */
    private static Cache<String, Country> iCourierLists = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();

    /**
     * 国家信息本地缓存化
     *
     * @return
     */
    public Country getCountry(String id) {
        try {
            String key = id;
            return iCourierLists.get(key, () -> countryMapper.inquireCountry(id));
        } catch (Exception e) {
            log.error("权限模块国家信息本地缓存失败 信息不存在:" + e);
        }
        return null;
    }
}
