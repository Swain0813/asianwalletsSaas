package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.ExchangeRateDTO;
import com.asianwallets.common.entity.ExchangeRate;
import com.asianwallets.common.vo.ExchangeRateVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 汇率Mapper接口
 */
@Repository
public interface ExchangeRateMapper extends BaseMapper<ExchangeRate> {

    /**
     * 多条件查询汇率信息
     *
     * @param exchangeRateDTO 汇率输入实体
     * @return 汇率输出实体集合
     */
    List<ExchangeRateVO> pageMultipleConditions(ExchangeRateDTO exchangeRateDTO);


    /**
     * 根据本位币种和目标币种和状态查询汇率信息
     *
     * @param localCurrency 本位币种
     * @param localCurrency 目标币种
     * @return 汇率实体
     */
    ExchangeRate selectByLocalCurrencyAndForeignCurrency(@Param("localCurrency") String localCurrency, @Param("foreignCurrency") String foreignCurrency);
}