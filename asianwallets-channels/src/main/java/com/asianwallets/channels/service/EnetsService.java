package com.asianwallets.channels.service;

import com.asianwallets.common.dto.enets.EnetsBankRequestDTO;
import com.asianwallets.common.dto.enets.EnetsOffLineRequestDTO;
import com.asianwallets.common.response.BaseResponse;

public interface EnetsService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate eNets网银收单接口
     **/
    BaseResponse eNetsDebitPay(EnetsBankRequestDTO enetsBankRequestDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate eNets线下收单接口
     **/
    BaseResponse eNetsPosCSBPay(EnetsOffLineRequestDTO enetsOffLineRequestDTO);

}
