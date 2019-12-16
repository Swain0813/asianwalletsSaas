package com.asianwallets.base.service;

import com.asianwallets.common.dto.SettleOrderDTO;
import com.asianwallets.common.dto.SettleOrderExportDTO;
import com.asianwallets.common.entity.SettleOrder;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 机构结算交易
 */
public interface SettleOrderService {

    /**
     * 机构结算交易一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    PageInfo<SettleOrder> pageSettleOrder(SettleOrderDTO settleOrderDTO);

    /**
     * 机构结算交易详情
     *
     * @param settleOrderDTO
     * @return
     */
    PageInfo<SettleOrder> pageSettleOrderDetail(SettleOrderExportDTO settleOrderDTO);

    /**
     * * 机构结算导出
     *
     * @param settleOrderDTO
     * @return
     */
    List<SettleOrder> exportSettleOrder(SettleOrderDTO settleOrderDTO);

}
