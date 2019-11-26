package com.asianwallets.base.service;

import com.asianwallets.common.dto.CountryDTO;
import com.asianwallets.common.vo.CountryVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 国家地区
 */
public interface CountryService {

    /**
     * 新增国家地区
     *
     * @param country
     * @return
     */
    int addCountry(CountryDTO country);

    /**
     * 修改国家
     *
     * @param country
     * @return
     */
    int updateCountry(CountryDTO country);

    /**
     * 查询国家
     *
     * @param country
     * @return
     */
    PageInfo pageCountry(CountryDTO country);

    /**
     * 禁用国家和地区
     *
     * @param country
     * @return
     */
    int banCountry(CountryDTO country);

    /**
     * 查询所有的国家地区
     *
     * @return
     */
    List<CountryVO> inquireAllCountry();
}
