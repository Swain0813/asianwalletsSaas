package com.asianwallets.clearing.service;

/**
 * @Author YangXu
 * @Date 2019/7/26
 * @Descripate 定时清算服务
 * @return
 **/

public interface ClearService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 以商户清算账户分组批次清算
     **/
    void ClearForGroupBatch();


}
