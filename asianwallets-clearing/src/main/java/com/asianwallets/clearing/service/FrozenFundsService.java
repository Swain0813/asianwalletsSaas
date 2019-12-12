package com.asianwallets.clearing.service;

import com.asianwallets.clearing.vo.CSFrozenFundsRequest;

public interface FrozenFundsService {



    /**
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 资金冻结/解冻接口
     * @return
     **/
    CSFrozenFundsRequest CSFrozenFunds(CSFrozenFundsRequest ffr);
}
