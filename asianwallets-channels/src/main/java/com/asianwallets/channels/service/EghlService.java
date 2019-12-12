package com.asianwallets.channels.service;

import com.asianwallets.common.dto.eghl.EGHLRequestDTO;
import com.asianwallets.common.response.BaseResponse;

public interface EghlService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate eghl收单接口
     **/
    BaseResponse eGHLPay(EGHLRequestDTO eghlRequestDTO);
}
