package com.asianwallets.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.ExchangeRateDTO;
import com.asianwallets.common.entity.ExchangeRate;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.ExchangeRateVO;
import com.asianwallets.base.dao.ExchangeRateMapper;
import com.asianwallets.base.service.ExchangeRateService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 汇率接口实现类
 */
@Service
@Transactional
public class ExchangeRateServiceImpl extends BaseServiceImpl<ExchangeRate> implements ExchangeRateService {

    @Autowired
    private ExchangeRateMapper exchangeRateMapper;

    @Autowired
    private RedisService redisService;

    /**
     * 添加汇率信息
     *
     * @param exchangeRateDTO 汇率输入实体
     * @param name            添加者姓名
     * @return 添加条数
     */
    @Override
    public int addExchangeRate(ExchangeRateDTO exchangeRateDTO, String name) {
        //判断输入的本币和外币是不是相同
        if(exchangeRateDTO.getLocalCurrency().equalsIgnoreCase(exchangeRateDTO.getForeignCurrency())){
            throw new BusinessException(EResultEnum.LOCAL_FOREIGN_CURRENCY_IS_SAME.getCode());
        }
        //先判断是否已存在相同本位币种与目标币种的换汇信息
        ExchangeRate dbRate = exchangeRateMapper.selectByLocalCurrencyAndForeignCurrency(exchangeRateDTO.getLocalCurrency(), exchangeRateDTO.getForeignCurrency());
        if (dbRate != null) {
            //数据库存在,禁用原来的汇率信息
            dbRate.setEnabled(false);
            dbRate.setUpdateTime(new Date());//修改时间
            dbRate.setOverdueTime(new Date());//失效时间
            dbRate.setModifier(name);//修改人
            exchangeRateMapper.updateByPrimaryKeySelective(dbRate);
        }
        ExchangeRate exchangeRate = new ExchangeRate();
        BeanUtils.copyProperties(exchangeRateDTO, exchangeRate);
        exchangeRate.setId(IDS.uuid2());//id
        exchangeRate.setEnabled(true);//启用
        exchangeRate.setCreateTime(new Date());//创建时间
        exchangeRate.setUsingTime(new Date());//生效时间
        exchangeRate.setCreator(name);//创建人
        //插入新的汇率信息
        int num = exchangeRateMapper.insert(exchangeRate);
        try {
            //更新汇率信息后添加的redis里
            redisService.set(AsianWalletConstant.EXCHANGERATE_CACHE_KEY.concat("_").concat(exchangeRate.getLocalCurrency()).concat("_").concat(exchangeRate.getForeignCurrency()),
                    JSON.toJSONString(exchangeRate));
        } catch (Exception e) {
            //Redis同步错误
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return num;
    }


    /**
     * 禁用汇率信息
     *
     * @param id   汇率id
     * @param name 禁用者名称
     * @return 禁用条数
     */
    @Override
    public int banExchangeRate(String id, String name) {
        ExchangeRate exchangeRate = exchangeRateMapper.selectByPrimaryKey(id);
        if (exchangeRate == null) {
            //汇率信息不存在
            throw new BusinessException(EResultEnum.EXCHANGERATE_IS_NOT_EXIST.getCode());
        }
        //禁用
        exchangeRate.setEnabled(false);
        //失效时间
        exchangeRate.setOverdueTime(new Date());
        //修改时间
        exchangeRate.setUpdateTime(new Date());
        //修改人
        exchangeRate.setModifier(name);
        int num = exchangeRateMapper.updateByPrimaryKeySelective(exchangeRate);
        try {
            //更新汇率信息后添加的redis里
            redisService.set(AsianWalletConstant.EXCHANGERATE_CACHE_KEY.concat("_").concat(exchangeRate.getLocalCurrency()).concat("_").concat(exchangeRate.getForeignCurrency()),
                    JSON.toJSONString(exchangeRate));
        } catch (Exception e) {
            //Redis同步错误
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return num;
    }


    /**
     * 多条件查询汇率信息
     *
     * @param exchangeRateDTO 汇率输入实体
     * @return 汇率输出实体集合
     */
    @Override
    public PageInfo<ExchangeRateVO> getByMultipleConditions(ExchangeRateDTO exchangeRateDTO) {
        return new PageInfo(exchangeRateMapper.pageMultipleConditions(exchangeRateDTO));
    }

}
