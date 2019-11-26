package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.CountryMapper;
import com.asianwallets.base.service.CountryService;
import com.asianwallets.common.dto.CountryDTO;
import com.asianwallets.common.entity.Country;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Predicate;

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
        if (StringUtils.isBlank(countryDTO.getParentId())) {
            if (!StringUtils.isBlank(countryDTO.getEnCountry()) && !StringUtils.isBlank(countryDTO.getCnCountry())) {/*
                Country c = countryMapper.selectByCnAndEnCountry(countryDTO);
                if (c == null) {
                    //国家
                    String cid = IDS.uuid2();
                    Country country = new Country();
                    country.setId(cid);
                    country.setEnabled(true);
                    country.setCnCountry(countryDTO.getCnCountry());
                    country.setEnCountry(countryDTO.getEnCountry());
                    country.setCreateTime(new Date());
                    country.setCreator(countryDTO.getCreator());
                    result = countryMapper.insert(country);
                    if (!StringUtils.isBlank(countryDTO.getEnState()) && !StringUtils.isBlank(countryDTO.getCnState())) {
                        Country s = countryMapper.selectByCnAndEnState(countryDTO);
                        if (s == null) {
                            //省份
                            Country state = new Country();
                            String sid = IDS.uuid2();
                            state.setId(sid);
                            state.setParentId(cid);
                            state.setEnabled(true);
                            state.setCnState(countryDTO.getCnState());
                            state.setEnState(countryDTO.getEnState());
                            state.setCreateTime(new Date());
                            state.setCreator(countryDTO.getCreator());
                            result = countryMapper.insert(state);
                            if (!StringUtils.isBlank(countryDTO.getCnCity()) && !StringUtils.isBlank(countryDTO.getEnCity())) {
                                Country ci = countryMapper.selectByCnAndEnCity(countryDTO);
                                if (ci == null) {
                                    //省份
                                    Country city = new Country();
                                    String ciid = IDS.uuid2();
                                    city.setId(ciid);
                                    city.setParentId(sid);
                                    city.setEnabled(true);
                                    city.setCnCity(countryDTO.getCnCity());
                                    city.setEnCity(countryDTO.getEnCity());
                                    city.setCreateTime(new Date());
                                    city.setCreator(countryDTO.getCreator());
                                    result = countryMapper.insert(city);
                                } else {
                                    throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
                                }
                            }
                        } else {
                            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
                        }
                    }
                } else {
                    throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
                }
            */

                result = addCountryIF(countryDTO, new AddCountry(), new AddState(), new AddCity());
            }
        } else {
            //父id存在
            Country parentCountry = countryMapper.selectByParentId(countryDTO.getParentId());
            if (parentCountry != null) {
                if (!StringUtils.isBlank(countryDTO.getEnState()) && !StringUtils.isBlank(countryDTO.getCnState())) {
                   /* Country s = countryMapper.selectByCnAndEnState(countryDTO);
                    if (s == null) {
                        //省份
                        Country state = new Country();
                        String sid = IDS.uuid2();
                        state.setId(sid);
                        state.setParentId(countryDTO.getParentId());
                        state.setEnabled(true);
                        state.setCnState(countryDTO.getCnState());
                        state.setEnState(countryDTO.getEnState());
                        state.setCreateTime(new Date());
                        state.setCreator(countryDTO.getCreator());
                        result = countryMapper.insert(state);
//                       result = addCountryTest(countryDTO, new AddState());
                        if (!StringUtils.isBlank(countryDTO.getCnCity()) && !StringUtils.isBlank(countryDTO.getEnCity())) {
                            Country ci = countryMapper.selectByCnAndEnCity(countryDTO);
                            if (ci == null) {
                                //省份
                                Country city = new Country();
                                String ciid = IDS.uuid2();
                                city.setId(ciid);
                                city.setParentId(sid);
                                city.setEnabled(true);
                                city.setCnCity(countryDTO.getCnCity());
                                city.setEnCity(countryDTO.getEnCity());
                                city.setCreateTime(new Date());
                                city.setCreator(countryDTO.getCreator());
                                result = countryMapper.insert(city);
                            } else {
                                throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
                            }
                        }
                    } else {
                        throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
                    }
                */
                    return addStateIF(countryDTO, new AddState(), new AddCity(), null);

                } else if (!StringUtils.isBlank(countryDTO.getCnCity()) && !StringUtils.isBlank(countryDTO.getEnCity())) {
                   /* Country ci = countryMapper.selectByCnAndEnCity(countryDTO);
                    if (ci == null) {
                        //省份
                        Country city = new Country();
                        String ciid = IDS.uuid2();
                        city.setId(ciid);
                        city.setParentId(countryDTO.getParentId());
                        city.setEnabled(true);
                        city.setCnCity(countryDTO.getCnCity());
                        city.setEnCity(countryDTO.getEnCity());
                        city.setCreateTime(new Date());
                        city.setCreator(countryDTO.getCreator());
                        result = countryMapper.insert(city);
                    } else {
                        throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
                    }*/
                    result = addCountry(countryDTO, this::checkCountryDTO);
                }
            } else {
                //信息不存在
                throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
            }
        }
        return result;
    }

    private int addCountry(CountryDTO countryDTO, Predicate<CountryDTO> pre) {
        int result = 0;
        if (pre.test(countryDTO)) {
            Country city = new Country();
            String ciid = IDS.uuid2();
            city.setId(ciid);
            city.setParentId(countryDTO.getParentId());
            city.setEnabled(true);
            city.setCnCity(countryDTO.getCnCity());
            city.setEnCity(countryDTO.getEnCity());
            city.setCreateTime(new Date());
            city.setCreator(countryDTO.getCreator());
            result = countryMapper.insert(city);
        }
        return result;
    }

    private boolean checkCountryDTO(CountryDTO countryDTO) {
        return countryMapper.selectByCnAndEnCity(countryDTO) == null;
    }

    interface AddCountryInterface {
        boolean accept(CountryDTO countryDTO);
    }

    class AddCountry implements AddCountryInterface {
        @Override
        public boolean accept(CountryDTO countryDTO) {
            return countryMapper.selectByCnAndEnCountry(countryDTO) == null;
        }
    }

    class AddState implements AddCountryInterface {
        @Override
        public boolean accept(CountryDTO countryDTO) {
            return countryMapper.selectByCnAndEnState(countryDTO) == null;
        }
    }

    class AddCity implements AddCountryInterface {
        @Override
        public boolean accept(CountryDTO countryDTO) {
            return countryMapper.selectByCnAndEnCity(countryDTO) == null;
        }
    }

    private int addCountryIF(CountryDTO countryDTO, AddCountryInterface addCountryInterface1,
                             AddCountryInterface addCountryInterface2, AddCountryInterface addCountryInterface3) {
        int result = 0;
        if (addCountryInterface1.accept(countryDTO)) {
            String cid = IDS.uuid2();
            Country country = new Country();
            country.setId(cid);
            country.setEnabled(true);
            country.setCnCountry(countryDTO.getCnCountry());
            country.setEnCountry(countryDTO.getEnCountry());
            country.setCreateTime(new Date());
            country.setCreator(countryDTO.getCreator());
            result = countryMapper.insert(country);
            if (!StringUtils.isBlank(countryDTO.getEnState()) && !StringUtils.isBlank(countryDTO.getCnState())) {
                result = addStateIF(countryDTO, addCountryInterface2, addCountryInterface3, cid);
            }
        }

        return result;
    }


    private int addStateIF(CountryDTO countryDTO, AddCountryInterface addCountryInterface1,
                           AddCountryInterface addCountryInterface2, String parentId) {
        int result = 0;
        if (addCountryInterface1.accept(countryDTO)) {
            Country state = new Country();
            String sid = IDS.uuid2();
            state.setId(sid);
            if (!StringUtils.isBlank(parentId)) {
                state.setParentId(parentId);
            } else {
                state.setParentId(countryDTO.getParentId());
            }
            state.setEnabled(true);
            state.setCnState(countryDTO.getCnState());
            state.setEnState(countryDTO.getEnState());
            state.setCreateTime(new Date());
            state.setCreator(countryDTO.getCreator());
            result = countryMapper.insert(state);
            if (!StringUtils.isBlank(countryDTO.getCnCity()) && !StringUtils.isBlank(countryDTO.getEnCity()) && StringUtils.isBlank(parentId)) {
                result = addCityIF(countryDTO, addCountryInterface2, null);
            } else {
                result = addCityIF(countryDTO, addCountryInterface2, sid);
            }
        }
        return result;
    }

    private int addCityIF(CountryDTO countryDTO, AddCountryInterface addCountryInterface, String parentId) {
        int result = 0;
        if (addCountryInterface.accept(countryDTO)) {
            Country city = new Country();
            String ciid = IDS.uuid2();
            city.setId(ciid);
            if (!StringUtils.isBlank(parentId)) {
                city.setParentId(parentId);
            } else {
                city.setParentId(countryDTO.getParentId());
            }
            city.setEnabled(true);
            city.setCnCity(countryDTO.getCnCity());
            city.setEnCity(countryDTO.getEnCity());
            city.setCreateTime(new Date());
            city.setCreator(countryDTO.getCreator());
            result = countryMapper.insert(city);
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
        if (countryMapper.selectByCountry(countryDTO) != null) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        Country country = new Country();
        BeanUtils.copyProperties(countryDTO, country);
        country.setUpdateTime(new Date());
        return countryMapper.updateByPrimaryKeySelective(country);
    }

    /**
     * 查询国家
     *
     * @param countryDTO
     * @return
     */
    @Override
    public PageInfo pageCountry(CountryDTO countryDTO) {
        countryDTO.setSort("cn_country");
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
        if (StringUtils.isBlank(countryDTO.getId()) || countryDTO.getEnabled() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (StringUtils.isBlank(countryDTO.getParentId())) {
            return countryMapper.banCountry(countryDTO);
        }
        /*  *//*int i = countryMapper.banCountry(countryDTO);*//*
        int i = countryMapper.banState(countryDTO);*/
        return 0;
    }
}
