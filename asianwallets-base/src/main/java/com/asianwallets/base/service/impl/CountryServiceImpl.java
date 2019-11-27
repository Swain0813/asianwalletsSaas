package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.CountryMapper;
import com.asianwallets.base.service.CountryService;
import com.asianwallets.common.dto.CountryDTO;
import com.asianwallets.common.entity.Country;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.AreaVO;
import com.asianwallets.common.vo.CountryVO;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName CountryServiceImpl
 * @Description 国家地区
 * @Author abc
 * @Date 2019/11/25 11:06
 * @Version 1.0
 */
@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryMapper countryMapper;

    /**
     * 新增国家地区
     *
     * @param countryDTO
     * @return
     */
    @Override
    public int addCountry(CountryDTO countryDTO) {
        int result = 0;
        Country country = new Country();
        if (StringUtils.isBlank(countryDTO.getId()) && !StringUtils.isBlank(countryDTO.getCountry())) {
            if (countryMapper.selectByName(countryDTO.getCountry()) == null) {
                //新增国家
                country.setAreaCode(countryDTO.getAreaCode());
                country.setName(countryDTO.getCountry());
                country.setLanguage(countryDTO.getLanguage());
                country.setEnabled(true);
                String randomInt = IDS.getRandomInt(20);
                country.setId(randomInt);
                country.setCreateTime(new Date());
                country.setCreator(countryDTO.getCreator());
                country.setRemark(countryDTO.getRemark());
                if (!StringUtils.isBlank(countryDTO.getArea())) {
                    //新增地区
                    Country areas = countryMapper.selectByAreaName(countryDTO.getArea());
                    if (areas == null) {
                        country.setAreaName(countryDTO.getArea() + ",");
                    }
                }
                result = countryMapper.insertSelective(country);
            }
        } else if (!StringUtils.isBlank(countryDTO.getArea()) && !StringUtils.isBlank(countryDTO.getId()) || !StringUtils.isBlank(countryDTO.getParentId())) {
            country = countryMapper.selectById(countryDTO);
            if (country != null) {
                //第一次新增地区
                country.setAreaName(country.getAreaName() + countryDTO.getArea() + ",");
                result = countryMapper.updateByPrimaryKeySelective(country);
            }
        }
        return result;
    }

    /**
     * 修改国家
     *
     * @param countryDTO
     * @return
     */
    @Override
    public int updateCountry(CountryDTO countryDTO) {
        if (StringUtils.isBlank(countryDTO.getId())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        Country c = countryMapper.selectByPrimaryKey(countryDTO.getId());
        if (c != null) {
            if (!StringUtils.isBlank(countryDTO.getCountry())) {
                c.setName(countryDTO.getCountry());
            } else {
                c.setName(countryDTO.getArea());
            }
            if (StringUtils.isBlank(countryDTO.getLanguage())) {
                c.setLanguage(countryDTO.getLanguage());
            }
            c.setUpdateTime(new Date());
            c.setModifier(countryDTO.getModifier());
        }
        return countryMapper.updateByPrimaryKeySelective(c);
    }

    /**
     * 查询国家
     *
     * @param countryDTO
     * @return
     */
    @Override
    public PageInfo pageCountry(CountryDTO countryDTO) {
        return new PageInfo<>(countryMapper.pageCountry(countryDTO));
    }

    /**
     * 禁用国家和地区
     *
     * @param countryDTO
     * @return
     */
    @Override
    public int banCountry(CountryDTO countryDTO) {
        return countryMapper.banCountry(countryDTO);
    }


    /**
     * 查询所有的国家地区
     *
     * @param language
     * @return
     */
    @Override
    public List<CountryVO> inquireAllCountry(String language) {
        ArrayList<CountryVO> countryVOS = new ArrayList<>();
        List<Country> countries = countryMapper.selectByLanguage(language);
        for (Country country : countries) {
            CountryVO countryVO = new CountryVO();
            ArrayList<AreaVO> areaVOS = new ArrayList<>();
            countryVO.setCountryName(country.getName());
            countryVO.setCountryId(country.getId());
            countryVO.setAreaCode(country.getAreaCode());
            if (!StringUtils.isBlank(country.getAreaName())) {
                String[] areas = country.getAreaName().split(",");
                for (String area : areas) {
                    AreaVO areaVO = new AreaVO();
                    areaVO.setAreaId(country.getId());
                    areaVO.setAreaParentId(country.getId());
                    areaVO.setAreaName(area);
                    areaVOS.add(areaVO);
                }
            }
            countryVO.setAreaVOS(areaVOS);
            countryVOS.add(countryVO);
        }
        return countryVOS;
    }

}
