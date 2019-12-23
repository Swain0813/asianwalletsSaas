package com.asianwallets.trade.service;

import com.asianwallets.trade.dto.OfflineLoginDTO;
import com.asianwallets.trade.dto.OfflineTradeDTO;
import com.asianwallets.trade.vo.CsbDynamicScanVO;

public interface OfflineTradeService {

    /**
     * 线下登录
     *
     * @param offlineLoginDTO 线下登录实体
     * @return token
     */
    String login(OfflineLoginDTO offlineLoginDTO);

    /**
     * 线下同机构CSB动态扫码
     *
     * @param offlineTradeDTO 线下交易输入实体
     * @return 线下同机构CSB动态扫码输出实体
     */
    CsbDynamicScanVO csbDynamicScan(OfflineTradeDTO offlineTradeDTO);

}
