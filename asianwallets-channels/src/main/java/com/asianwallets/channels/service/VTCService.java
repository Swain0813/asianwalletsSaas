package com.asianwallets.channels.service;

import com.asianwallets.common.dto.vtc.VTCRequestDTO;
import com.asianwallets.common.response.BaseResponse;

public interface VTCService {

    /**
     * vtcPay收单接口
     *
     * @param vtcRequestDTO
     * @return
     */
    BaseResponse vtcPay(VTCRequestDTO vtcRequestDTO);
}
