package com.asianwallets.base.service;
import com.asianwallets.common.dto.ExchangeRateDTO;
import com.github.pagehelper.PageInfo;

/**
 * 汇率业务接口
 */
public interface ExchangeRateService {

    /**
     * 添加汇率信息
     *
     * @param exchangeRateDTO 汇率输入实体
     * @param name 添加者姓名
     * @return 添加条数
     */
    int addExchangeRate(ExchangeRateDTO exchangeRateDTO, String name);

    /**
     * 禁用汇率信息
     * @param id 汇率id
     * @param name 禁用者名称
     * @return 禁用条数
     */
    int banExchangeRate(String id, String name);

    /**
     * 查询汇率信息
     *
     * @param exchangeRateDTO 汇率输入实体
     * @return 汇率输出实体集合
     */
    PageInfo getByMultipleConditions(ExchangeRateDTO exchangeRateDTO);

}
