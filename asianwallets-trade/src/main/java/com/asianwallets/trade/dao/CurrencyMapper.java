package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Currency;
import com.asianwallets.trade.vo.PosCurrencyVO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CurrencyMapper extends BaseMapper<Currency> {

    /**
     * 根据币种编码查询默认值
     *
     * @param currency 币种
     * @return 默认值
     */
    Currency selectByCurrency(String currency);

    /**
     * 查询所有币种对应的默认值
     *
     * @return 币种对应默认值集合
     */
    @Select("select code as currency,defaults from currency where enabled = 1")
    List<PosCurrencyVO> selectAllCodeAndDefaults();
}