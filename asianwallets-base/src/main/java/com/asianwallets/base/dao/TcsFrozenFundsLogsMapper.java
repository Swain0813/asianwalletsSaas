package com.asianwallets.base.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.TcsFrozenFundsLogs;
import org.springframework.stereotype.Repository;

@Repository
public interface TcsFrozenFundsLogsMapper extends BaseMapper<TcsFrozenFundsLogs> {

    /**
     * 根据账户号查询冻结记录
     * @param mvaccountId
     * @return
     */
    TcsFrozenFundsLogs selectByMvaccountId(String mvaccountId);

    /**
     * 若冻结记录已存在，则更新
     * @param ffl
     * @return
     */
    int updateFrozenByMIO(TcsFrozenFundsLogs ffl);
}