package com.asianwallets.channels.service;

import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.response.BaseResponse;


public interface THService {

    /**
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate 通华退款
     * @return
     **/
    BaseResponse thRefund(ISO8583DTO thRefundDTO);
}
