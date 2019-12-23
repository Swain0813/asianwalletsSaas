package com.asianwallets.channels.service;


import com.asianwallets.common.dto.ad3.AD3CSBScanPayDTO;
import com.asianwallets.common.dto.ad3.AD3ONOFFRefundDTO;
import com.asianwallets.common.dto.ad3.AD3RefundDTO;
import com.asianwallets.common.dto.ad3.SendAdRefundDTO;
import com.asianwallets.common.response.BaseResponse;

public interface Ad3Service {

    /**
     * AD3线下CSB
     *
     * @param ad3CSBScanPayDTO   AD3线下CSB输入实体
     * @return BaseResponse
     */
    BaseResponse offlineCsb(AD3CSBScanPayDTO ad3CSBScanPayDTO);

    /**
     * @Author YangXu
     * @Date 2019/12/20
     * @Descripate AD3线下退款接口
     * @return
     **/
    BaseResponse offlineRefund(AD3ONOFFRefundDTO ad3RefundDTO);

    /**
     * @Author YangXu
     * @Date 2019/12/20
     * @Descripate AD3线上退款接口
     * @return
     **/
    BaseResponse onlineRefund(AD3ONOFFRefundDTO sendAdRefundDTO);
}
