package com.asianwallets.base.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.SettleOrderDTO;
import com.asianwallets.common.entity.SettleOrder;
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
}
