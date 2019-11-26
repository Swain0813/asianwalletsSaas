package com.asianwallets.task.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.ExchangeRate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

/**
 * @Author XuWenQi
 * @Date 2019/1/25 14:20
 * @Descripate 汇率Mapper接口
 */
@Repository
public interface ExchangeRateMapper extends BaseMapper<ExchangeRate> {

    /**
     * 根据本币与外币禁用汇率
     *
     * @param modifier        修改者
     * @param localCurrency   本位币种
     * @param foreignCurrency 目标币种
     * @return 修改条数
     */
    int updateStatusByLocalCurrencyAndForeignCurrency(@Param("localCurrency") String localCurrency, @Param("foreignCurrency") String foreignCurrency, @Param("modifier") String modifier);

    /**
     * 根据创建时间查询汇率信息
     *
     * @param date 创建日期
     * @return 汇率实体集合
     */
    List<ExchangeRate> selectByCreateTimeAndCreator(@Param("date") String date, @Param("creator") String creator);


    /**
     * 根据订单币种查询汇率
     *
     * @param orderCurrency 订单币种
     * @param tradeCurrency 交易币种
     * @return 汇率值
     */
    ExchangeRate selectRateByOrderCurrencyAndTradeCurrency(@Param("orderCurrency") String orderCurrency, @Param("tradeCurrency") String tradeCurrency);

    /**
     * 查询汇率
     *
     * @param orderCurrency
     * @param tradeCurrency
     * @param start
     * @return
     */
    ExchangeRate selectRateByOrderCurrencyAndTradeCurrencyAndTime(@Param("orderCurrency") String orderCurrency, @Param("tradeCurrency") String tradeCurrency, @Param("start") Date start);
}