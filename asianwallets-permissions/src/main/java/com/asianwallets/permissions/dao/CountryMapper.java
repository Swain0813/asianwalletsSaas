package com.asianwallets.permissions.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Country;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 国家
 */
@Repository
public interface CountryMapper extends BaseMapper<Country> {

    /**
     * 查询所有的国家地区
     *
     * @return
     * @param id
     */
    Country inquireCountry(@Param("id") String id);
}