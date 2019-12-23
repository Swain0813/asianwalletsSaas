package com.asianwallets.channels.service;


import com.asianwallets.common.dto.megapay.*;
import com.asianwallets.common.response.BaseResponse;

public interface MegaPayService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate megaPay—THB收单接口
     **/
    BaseResponse megaPayTHB(MegaPayRequestDTO megaPayRequestDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate megaPay—IDR收单接口
     **/
    BaseResponse megaPayIDR(MegaPayIDRRequestDTO megaPayIDRRequestDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/5/28
     * @Descripate nextPos收单接口
     **/
    BaseResponse nextPosCsb(NextPosRequestDTO nextPosRequestDTO);

    /**
     * NextPos查询接口
     *
     * @param nextPosQueryDTO nextPos查询实体
     * @return BaseResponse
     */
    BaseResponse nextPosQuery(NextPosQueryDTO nextPosQueryDTO);

    /**
     * NextPos退款接口
     *
     * @param nextPosRefundDTO nextPos退款实体
     * @return BaseResponse
     */
    BaseResponse nextPosRefund(NextPosRefundDTO nextPosRefundDTO);

    /**
     * MegaPay查询接口
     *
     * @param megaPayQueryDTO megaPay查询
     * @return
     */
    BaseResponse megaPayQuery(MegaPayQueryDTO megaPayQueryDTO);
}
