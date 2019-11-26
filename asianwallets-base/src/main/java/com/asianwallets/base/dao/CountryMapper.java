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
     */
    List<CountryVO> inquireAllCountry();

    /**
     * 通过名称查询国家地区
     *
     * @param cnName
     * @param enName
     * @return
     */
    Country selectByCnOrEnName(@Param("cnName") String cnName, @Param("enName") String enName);

    /**
     * 禁用地区
     *
     * @param countryDTO
     * @return
     */
    int banCountry(CountryDTO countryDTO);
}