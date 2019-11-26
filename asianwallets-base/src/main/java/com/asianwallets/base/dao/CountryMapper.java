package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.CountryDTO;
import com.asianwallets.common.entity.Country;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 国家
 */
@Repository
public interface CountryMapper extends BaseMapper<Country> {
   /* int deleteByPrimaryKey(String id);

    int insert(Country record);

    int insertSelective(Country record);

    Country selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Country record);

    int updateByPrimaryKey(Country record);*/

    /**
     * 通过国家名称去查询国家
     *
     * @param countryDTO
     * @return
     */
    Country selectByCnAndEnCountry(CountryDTO countryDTO);

    /**
     * 查询省份
     *
     * @param countryDTO
     * @return
     */
    Country selectByCnAndEnState(CountryDTO countryDTO);

    /**
     * 查询城市
     *
     * @param countryDTO
     * @return
     */
    Country selectByCnAndEnCity(CountryDTO countryDTO);

    /**
     * 通过ParentId 查询
     *
     * @param parentId
     * @return
     */
    Country selectByParentId(@Param("parentId") String parentId);

    /**
     * 查询国家
     *
     * @param countryDTO
     * @return
     */
    Country selectByCountry(CountryDTO countryDTO);

    /**
     * 分页查询国家
     *
     * @param countryDTO
     * @return
     */
    List<Country> pageCountry(CountryDTO countryDTO);

    /**
     * 禁用国家
     *
     * @param countryDTO
     * @return
     */
    int banCountry(CountryDTO countryDTO);
}