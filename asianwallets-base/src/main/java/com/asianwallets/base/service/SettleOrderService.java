package com.asianwallets.base.service;

import com.asianwallets.common.dto.GroupReviewSettleDTO;
import com.asianwallets.common.dto.ReviewSettleDTO;
import com.asianwallets.common.dto.SettleOrderDTO;
import com.asianwallets.common.dto.WithdrawalDTO;
import com.asianwallets.common.entity.SettleOrder;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 结算交易
 */
public interface SettleOrderService {

    /**
     * 结算交易一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    PageInfo<SettleOrder> pageSettleOrder(SettleOrderDTO settleOrderDTO);

    /**
     * 查询集团提款
     *
     * @param settleOrderDTO
     * @return
     */
    PageInfo<SettleOrder> pageGroupSettleOrder(SettleOrderDTO settleOrderDTO);

    /**
     * 结算交易详情
     *
     * @param settleOrderDTO
     * @return
     */
    PageInfo<SettleOrder> pageSettleOrderDetail(SettleOrderDTO settleOrderDTO);

    /**
     * 结算导出
     *
     * @param settleOrderDTO
     * @return
     */
    List<SettleOrder> exportSettleOrder(SettleOrderDTO settleOrderDTO);

    /**
     * 结算审核
     *
     * @param reviewSettleDTO
     * @return
     */
    int reviewSettlement(ReviewSettleDTO reviewSettleDTO);

    /**
     * 集团商户结算审核
     *
     * @param reviewSettleDTO
     * @return
     */
    int reviewGroupSettlement(GroupReviewSettleDTO reviewSettleDTO);

    /**
     * 手动提款
     *
     * @param withdrawalDTO
     * @param userName
     */
    String withdrawal(WithdrawalDTO withdrawalDTO, String userName);

}
