package com.asianwallets.trade.service.impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.dto.CsbDynamicScanDTO;
import com.asianwallets.trade.service.OfflineTradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OfflineTradeServiceImpl implements OfflineTradeService {

    /**
     * 线下同机构CSB动态扫码
     *
     * @param csbDynamicScanDTO 线下同机构CSB动态扫码输入实体
     * @return BaseResponse
     */
    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public BaseResponse csbDynamicScan(CsbDynamicScanDTO csbDynamicScanDTO) {
        return null;
    }
}
