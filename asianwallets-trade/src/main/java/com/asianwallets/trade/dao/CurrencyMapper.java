package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Currency;
import org.springframework.stereotype.Repository;


@Repository
public interface CurrencyMapper extends BaseMapper<Currency> {

    /**
     * 根据币种编码查询默认值
     *
     * @param currency 币种
     * @return 默认值
     */
    String selectByCurrency(String currency);
}