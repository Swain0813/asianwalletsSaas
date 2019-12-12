package com.asianwallets.clearing.service;


/**
 * @Author YangXu
 * @Date 2019/7/26
 * @Descripate 定时结算服务
 * @return
 **/
public interface SettleService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 结算批次处理
     **/
    void SettlementForBatch();

}
