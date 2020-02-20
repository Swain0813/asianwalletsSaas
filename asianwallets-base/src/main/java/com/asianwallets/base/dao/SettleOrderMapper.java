package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.GroupReviewSettleDTO;
import com.asianwallets.common.dto.SettleOrderDTO;
import com.asianwallets.common.entity.SettleOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 结算交易Mapper
 */
@Repository
public interface SettleOrderMapper extends BaseMapper<SettleOrder> {

    /**
     * 结算交易一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    List<SettleOrder> pageSettleOrder(SettleOrderDTO settleOrderDTO);

    /**
     * 结算交易详情
     *
     * @param settleOrderDTO
     * @return
     */
    List<SettleOrder> pageSettleOrderDetail(SettleOrderDTO settleOrderDTO);


    /**
     * 查询结算详情导出用
     *
     * @param settleOrderDTO
     * @return
     */
    List<SettleOrder> exportSettleOrderInfo(SettleOrderDTO settleOrderDTO);

    /**
     * 集团商户提款查询
     *
     * @param merchantIds
     * @param txncurrency
     * @param bankCodeCurrency
     * @param startDate
     * @param endDate
     * @return
     */
    List<SettleOrder> pageGroupSettleOrder(@Param("list") List<String> merchantIds, @Param("txncurrency") String txncurrency, @Param("bankCodeCurrency") String bankCodeCurrency, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 通过批次号更新settleOrder
     *
     * @param reviewSettleDTO
     * @return
     */
    int updateByBatchNo(GroupReviewSettleDTO reviewSettleDTO);
}
