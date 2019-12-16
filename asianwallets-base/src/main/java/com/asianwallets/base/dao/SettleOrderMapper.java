package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.SettleOrderDTO;
import com.asianwallets.common.dto.SettleOrderExportDTO;
import com.asianwallets.common.entity.SettleOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 机构结算交易Mapper
 */
@Repository
public interface SettleOrderMapper extends BaseMapper<SettleOrder> {

    /**
     * 机构结算交易一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    List<SettleOrder> pageSettleOrder(SettleOrderDTO settleOrderDTO);

    /**
     * 机构结算交易详情
     *
     * @param settleOrderDTO
     * @return
     */
    List<SettleOrder> pageSettleOrderDetail(SettleOrderExportDTO settleOrderDTO);


    /**
     * 查询机构结算详情导出用
     *
     * @param settleOrderDTO
     * @return
     */
    List<SettleOrder> exportSettleOrderInfo(SettleOrderDTO settleOrderDTO);
}
