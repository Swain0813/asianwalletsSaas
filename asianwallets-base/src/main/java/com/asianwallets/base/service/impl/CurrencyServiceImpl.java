package com.asianwallets.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.CurrencyMapper;
import com.asianwallets.base.service.CurrencyService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.CurrencyDTO;
import com.asianwallets.common.entity.Currency;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.CurrencyExportVO;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

import static com.asianwallets.common.utils.ReflexClazzUtils.getNullPropertyNames;

/**
 * @ClassName CurrencyServiceImpl
 * @Description 币种
 * @Author abc
 * @Date 2019/11/22 6:36
 * @Version 1.0
 */
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private RedisService redisService;

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
        //国家名称
        currency.setRemark(currencyDTO.getRemark());
        currency.setId(IDS.uuid2());
        currency.setCreateTime(new Date());
        currency.setEnabled(true);
        int result = currencyMapper.insert(currency);
        if (result != 0) {
            redisService.set(AsianWalletConstant.CURRENCY_CACHE_KEY.concat("_").concat(currencyDTO.getCode()), JSON.toJSONString(currency));
        }
        return result;
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
        Currency c = currencyMapper.selectById(currencyDTO.getId());
        if (c == null) {
            throw new BusinessException(EResultEnum.INFORMATION_DOES_NOT_EXIST.getCode());
        }
        BeanUtils.copyProperties(currencyDTO, c, getNullPropertyNames(currencyDTO));
        c.setUpdateTime(new Date());
        int result = currencyMapper.updateByPrimaryKeySelective(c);
        if (result != 0) {
            redisService.set(AsianWalletConstant.CURRENCY_CACHE_KEY.concat("_").concat(c.getCode()), JSON.toJSONString(c));
        }
        return result;
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
        int result = 0;
        Currency c = currencyMapper.selectById(currencyDTO.getId());
        if (c != null) {
            c.setEnabled(currencyDTO.getEnabled());
            c.setUpdateTime(new Date());
            result = currencyMapper.updateByPrimaryKeySelective(c);
            if (result != 0) {
                redisService.set(AsianWalletConstant.CURRENCY_CACHE_KEY.concat("_").concat(c.getCode()), JSON.toJSONString(c));
            }
        }
        return result;
    }

    /**
     * 查询所有币种
     *
     * @return
     */
    @Override
    public List<Currency> inquireAllCurrency() {
        Example example = new Example(Currency.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("enabled", true);
        return currencyMapper.selectByExample(example);
    }

    /**
     * 导出币种信息用
     * @param currencyDTO
     * @return
     */
    @Override
    public List<CurrencyExportVO> exportCurrency(CurrencyDTO currencyDTO){
        List<CurrencyExportVO> currencyExportVOLists = currencyMapper.exportCurrency(currencyDTO);
        return currencyExportVOLists;
    }
}
