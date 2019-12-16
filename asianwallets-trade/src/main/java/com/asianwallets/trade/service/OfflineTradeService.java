package com.asianwallets.trade.service;

import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.dto.CsbDynamicScanDTO;

public interface OfflineTradeService {

    /**
     * 线下同机构CSB动态扫码
     *
     * @param csbDynamicScanDTO 线下同机构CSB动态扫码输入实体
     * @return BaseResponse
     */
    BaseResponse csbDynamicScan(CsbDynamicScanDTO csbDynamicScanDTO);
}
