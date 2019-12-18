package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.ExchangeRateDTO;
import com.asianwallets.common.entity.ExchangeRate;
import com.asianwallets.common.vo.ExchangeRateVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRateMapper extends BaseMapper<ExchangeRate> {

    /**
     * 根据本位币种和目标币种和状态查询汇率信息
     *
     * @param localCurrency 本位币种
     * @param localCurrency 目标币种
     * @return 汇率实体
     */
    ExchangeRate selectByLocalCurrencyAndForeignCurrency(@Param("localCurrency") String localCurrency, @Param("foreignCurrency") String foreignCurrency);
}