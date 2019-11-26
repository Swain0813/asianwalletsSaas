package com.asianwallets.permissions.service;

import com.asianwallets.common.dto.OperationLogDTO;
import com.asianwallets.common.entity.OperationLog;
import com.github.pagehelper.PageInfo;

/**
 * 操作日志模块相关业务
 */
public interface OperationLogService {

    /**
     * 添加操作日志
     *
     * @param operationLogDTO
     * @return
     */
    int addOperationLog(OperationLogDTO operationLogDTO);

    /**
     * 查询所有操作日志
     *
     * @param operationLogDTO
     * @return
     */
    PageInfo<OperationLog> pageOperationLog(OperationLogDTO operationLogDTO);
}
