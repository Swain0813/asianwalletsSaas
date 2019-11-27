package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.CountryDTO;
import com.asianwallets.common.entity.Country;
import com.asianwallets.common.vo.CountryVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 国家
 */
@Repository
public interface CountryMapper extends BaseMapper<Country> {

    /**
     * 分页查询国家
     *
     * @param countryDTO
     * @return
     */
    List<Country> pageCountry(CountryDTO countryDTO);

    /**
     * 查询所有的国家地区
     *
     * @return
     * @param language
     */
    List<CountryVO> inquireAllCountry(@Param("language") String language);

    /**
     * 通过名称查询
     *
     * @param name
     * @return
     */
    Country selectByName(@Param("name") String name);

    /**
     * 禁用地区
     *
     * @param countryDTO
     * @return
     */
    int banCountry(CountryDTO countryDTO);
}