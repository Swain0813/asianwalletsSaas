package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.CountryMapper;
import com.asianwallets.base.service.CountryService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.CountryDTO;
import com.asianwallets.common.entity.Country;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.CountryVO;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (StringUtils.isBlank(countryDTO.getParentId()) && !StringUtils.isBlank(countryDTO.getCountry())) {
            if (countryMapper.selectByName(countryDTO.getCountry()) == null) {
                //新增国家
                Country country = new Country();
                country.setAreaCode(countryDTO.getAreaCode());
                country.setName(countryDTO.getCountry());
                country.setLanguage(countryDTO.getLanguage());
                country.setType(AsianWalletConstant.COUNTRY);
                country.setEnabled(true);
                //国家ID
                String cid = IDS.getRandomInt(20);
                country.setId(cid);
                country.setCreateTime(new Date());
                country.setCreator(countryDTO.getCreator());
                country.setRemark(countryDTO.getRemark());
                result = countryMapper.insert(country);
                if (!StringUtils.isBlank(countryDTO.getArea())) {
                    //新增地区
                    Country areas = countryMapper.selectByName(countryDTO.getArea());
                    if (areas == null) {
                        //只新增地区
                        Country area = new Country();
                        area.setParentId(cid);
                        area.setName(countryDTO.getArea());
                        area.setLanguage(countryDTO.getLanguage());
                        area.setType(AsianWalletConstant.AREA);
                        area.setEnabled(true);
                        area.setId(IDS.getRandomInt(20));
                        area.setCreateTime(new Date());
                        area.setCreator(countryDTO.getCreator());
                        area.setRemark(countryDTO.getRemark());
                        result = countryMapper.insert(area);
                    }
                }
            }
        } else if (!StringUtils.isBlank(countryDTO.getArea()) && !StringUtils.isBlank(countryDTO.getParentId())) {
            Country country = countryMapper.selectByPrimaryKey(countryDTO.getParentId());
            Country areas = countryMapper.selectByName(countryDTO.getArea());
            if (country != null && country.getType().equals(AsianWalletConstant.COUNTRY) && areas == null) {
                //只新增地区
                Country area = new Country();
                area.setParentId(country.getId());
                area.setName(countryDTO.getArea());
                area.setLanguage(countryDTO.getLanguage());
                area.setType(AsianWalletConstant.AREA);
                area.setEnabled(true);
                area.setId(IDS.getRandomInt(20));
                area.setCreateTime(new Date());
                area.setCreator(countryDTO.getCreator());
                area.setRemark(countryDTO.getRemark());
                result = countryMapper.insert(area);
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
     * @return
     * @param language
     */
    @Override
    public List<CountryVO> inquireAllCountry(String language) {
        return countryMapper.inquireAllCountry(language);
    }

}
