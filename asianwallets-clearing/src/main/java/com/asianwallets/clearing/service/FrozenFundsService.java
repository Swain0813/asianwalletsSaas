package com.asianwallets.clearing.service;


import com.asianwallets.common.vo.clearing.CSFrozenFundsRequest;

public interface FrozenFundsService {



    /**
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 资金冻结/解冻接口
     * @return
     **/
    CSFrozenFundsRequest CSFrozenFunds(CSFrozenFundsRequest ffr);
}
