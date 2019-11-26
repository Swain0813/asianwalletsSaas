package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.CurrencyDTO;
import com.asianwallets.common.entity.Currency;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyMapper extends BaseMapper<Currency> {

    /**
     * 依据Code与Name查询币种
     *
     * @param currencyDTO
     * @return
     */
    int selectByCodeAndName(CurrencyDTO currencyDTO);

    /**
     * 通过ID查找币种l
     *
     * @param id
     * @return
     */
    Currency selectById(@Param("id") String id);

    /**
     * 分页查询币种
     *
     * @param currencyDTO
     * @return
     */
    List<Currency> pageCurrency(CurrencyDTO currencyDTO);
}