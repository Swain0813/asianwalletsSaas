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
    /**
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate 通华查询
     * @return
     **/
    BaseResponse thQuerry(ISO8583DTO thRefundDTO);

    /**
     * 通华CSB
     * @param iso8583DTO
     * @return
     */
    BaseResponse thCSB(ISO8583DTO iso8583DTO);

    /**
     * 通华BSC
     * @param iso8583DTO
     * @return
     */
    BaseResponse thBSC(ISO8583DTO iso8583DTO);
}
