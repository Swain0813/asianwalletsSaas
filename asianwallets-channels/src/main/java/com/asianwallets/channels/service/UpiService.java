package com.asianwallets.channels.service;

import com.asianwallets.common.dto.upi.UpiDTO;
import com.asianwallets.common.response.BaseResponse;

public interface UpiService {

    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate upi支付
     **/
    BaseResponse upiPay(UpiDTO upiDTO);
    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate upi查询
     **/
    BaseResponse upiQueery(UpiDTO upiDTO);
    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate upi退款
     **/
    BaseResponse upiRefund(UpiDTO upiDTO);
    /**
     * @return
     * @Author YangXu
     * @Date 2020/5/7
     * @Descripate upi撤销（当天订单退款用撤销）
     **/
    BaseResponse upiCancel(UpiDTO upiDTO);



}
