package com.asianwallets.permissions.service;

import cn.hutool.poi.excel.ExcelWriter;
import com.asianwallets.common.entity.SettleOrder;

import java.util.ArrayList;

/**
 * 结算信息导出模块
 */
public interface SettleOrderFeignService {
    /**
     * 其他系统结算表导出
     * @param settleOrder
     * @param clazz
     * @return
     */
    ExcelWriter getInsExcelWriter(ArrayList<SettleOrder> settleOrder, Class clazz);
}
