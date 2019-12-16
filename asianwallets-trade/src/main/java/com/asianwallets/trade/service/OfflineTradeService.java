package com.asianwallets.trade.service;

import com.asianwallets.trade.dto.CsbDynamicScanDTO;
import com.asianwallets.trade.vo.CsbDynamicScanVO;

public interface OfflineTradeService {

    /**
     * 线下同机构CSB动态扫码
     *
     * @param csbDynamicScanDTO 线下同机构CSB动态扫码输入实体
     * @return 线下同机构CSB动态扫码输出实体
     */
    CsbDynamicScanVO csbDynamicScan(CsbDynamicScanDTO csbDynamicScanDTO);
}
