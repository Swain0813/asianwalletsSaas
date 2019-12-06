package com.asianwallets.base.service;

import com.asianwallets.common.dto.CurrencyDTO;
import com.asianwallets.common.entity.Currency;
import com.asianwallets.common.vo.CurrencyExportVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 币种管理
 */
public interface CurrencyService {

    /**
     * 添加币种
     *
     * @param currencyDTO
     * @return
     */
    int addCurrency(CurrencyDTO currencyDTO);

    /**
     * 修改币种
     *
     * @param currencyDTO
     * @return
     */
    int updateCurrency(CurrencyDTO currencyDTO);

    /**
     * 查询币种
     *
     * @param currencyDTO
     * @return
     */
    PageInfo<Currency> pageCurrency(CurrencyDTO currencyDTO);

    /**
     * 启用禁用币种
     *
     * @param currencyDTO
     * @return
     */
    int banCurrency(CurrencyDTO currencyDTO);

    /**
     * 查询所有币种
     *
     * @return
     */
    List<Currency> inquireAllCurrency();

    /**
     * 导出币种信息用
     * @param currencyDTO
     * @return
     */
    List<CurrencyExportVO> exportCurrency(CurrencyDTO currencyDTO);
}
