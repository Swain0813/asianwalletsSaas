package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.OperationLogDTO;
import com.asianwallets.common.entity.OperationLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    /**
     * 查询所有操作日志
     *
     * @param operationLogDTO
     * @return
     */
    List<OperationLog> pageOperLog(OperationLogDTO operationLogDTO);
}