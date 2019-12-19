package com.asianwallets.base.service.impl;
import com.asianwallets.base.dao.SettleOrderMapper;
import com.asianwallets.base.service.SettleOrderService;
import com.asianwallets.common.dto.SettleOrderDTO;
import com.asianwallets.common.entity.SettleOrder;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 结算交易
 */
@Transactional
@Service
@Slf4j
public class SettleOrderServiceImpl implements SettleOrderService {

    @Autowired
    private SettleOrderMapper settleOrderMapper;

    /**
     * 结算交易一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public PageInfo<SettleOrder> pageSettleOrder(SettleOrderDTO settleOrderDTO) {
        return new PageInfo<SettleOrder>(settleOrderMapper.pageSettleOrder(settleOrderDTO));

    }

    /**
     * 结算交易详情
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public PageInfo<SettleOrder> pageSettleOrderDetail(SettleOrderDTO settleOrderDTO) {
        return new PageInfo<SettleOrder>(settleOrderMapper.pageSettleOrderDetail(settleOrderDTO));

    }

    /**
     * 结算导出
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public List<SettleOrder> exportSettleOrder(SettleOrderDTO settleOrderDTO) {
        return settleOrderMapper.exportSettleOrderInfo(settleOrderDTO);
    }

}
