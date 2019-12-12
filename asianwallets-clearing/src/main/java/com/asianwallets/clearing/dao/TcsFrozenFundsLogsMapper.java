package com.asianwallets.clearing.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.TcsFrozenFundsLogs;
import org.springframework.stereotype.Repository;

@Repository
public interface TcsFrozenFundsLogsMapper extends BaseMapper<TcsFrozenFundsLogs> {

    /**
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 根据账户号查询冻结记录
     * @return
     **/
    TcsFrozenFundsLogs selectByMvaccountId(String mvaccountId);

    /**
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 若冻结记录已存在，则更新
     * @return
     **/
    int updateFrozenByMIO(TcsFrozenFundsLogs ffl);
}