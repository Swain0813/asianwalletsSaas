package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.CurrencyMapper;
import com.asianwallets.base.service.CurrencyService;
import com.asianwallets.common.dto.CurrencyDTO;
import com.asianwallets.common.entity.Currency;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ClassName CurrencyServiceImpl
 * @Description 币种
 * @Author abc
 * @Date 2019/11/22 6:36
 * @Version 1.0
 */
@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyMapper currencyMapper;

    /**
     * 添加币种
     *
     * @param currencyDTO
     * @return
     */
    @Override
    public int addCurrency(CurrencyDTO currencyDTO) {
        if (StringUtils.isBlank(currencyDTO.getName()) || StringUtils.isBlank(currencyDTO.getCode())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (currencyMapper.selectByCodeAndName(currencyDTO) != 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        Currency currency = new Currency();
        BeanUtils.copyProperties(currencyDTO, currency);
        //国家ID
        currency.setRemark(currencyDTO.getRemark());
        currency.setId(IDS.uuid2());
        currency.setCreateTime(new Date());
        currency.setEnabled(true);
        return currencyMapper.insert(currency);
    }

    /**
     * 修改币种
     *
     * @param currencyDTO
     * @return
     */
    @Override
    public int updateCurrency(CurrencyDTO currencyDTO) {
        if (StringUtils.isBlank(currencyDTO.getId())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (currencyMapper.selectById(currencyDTO.getId()) == null) {
            throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
        }
        Currency currency = new Currency();
        BeanUtils.copyProperties(currencyDTO, currency);
        currency.setUpdateTime(new Date());
        return currencyMapper.updateByPrimaryKeySelective(currency);
    }

    /**
     * 查询币种
     *
     * @param currencyDTO
     * @return
     */
    @Override
    public PageInfo<Currency> pageCurrency(CurrencyDTO currencyDTO) {
        return new PageInfo<Currency>(currencyMapper.pageCurrency(currencyDTO));
    }

    /**
     * 启用禁用币种
     *
     * @param currencyDTO
     * @return
     */
    @Override
    public int banCurrency(CurrencyDTO currencyDTO) {
        if (StringUtils.isBlank(currencyDTO.getId()) || currencyDTO.getEnabled() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        Currency currency = new Currency();
        BeanUtils.copyProperties(currencyDTO, currency);
        currency.setUpdateTime(new Date());
        return currencyMapper.updateByPrimaryKeySelective(currency);
    }

    /**
     * 查询所有币种
     *
     * @return
     */
    @Override
    public List<Currency> inquireAllCurrency() {
        return currencyMapper.selectAll();
    }
}
