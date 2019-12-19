package com.asianwallets.clearing.service;

import com.asianwallets.common.vo.clearing.FundChangeDTO;

public interface IntoAccountService {


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/25
     * @Descripate 资金变动接口
     **/
    FundChangeDTO intoAndOutMerhtAccount(FundChangeDTO fundChangeDTO);
}
