package com.asianwallets.task.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.TcsFrozenFundsLogs;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 预约冻结记录
 */
@Repository
public interface TcsFrozenFundsLogsMapper extends BaseMapper<TcsFrozenFundsLogs> {

    /**
     * 查询所有待冻结的预约冻结记录
     * @return
     */
    List<TcsFrozenFundsLogs> getTcsFrozenFundsLogs();

    /**
     * 根据预约冻结记录的id更新待冻结的记录
     * @param id
     * @return
     */
    @Update("update reconciliation set status = 1 ,updateDatetime=NOW() where id = #{id}")
    int updateTcsFrozenFundsLogsById(String id);
}