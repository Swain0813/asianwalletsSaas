package com.asianwallets.channels.service;


import com.asianwallets.common.dto.ad3.AD3BSCScanPayDTO;
import com.asianwallets.common.dto.ad3.AD3CSBScanPayDTO;
import com.asianwallets.common.dto.ad3.AD3ONOFFRefundDTO;
import com.asianwallets.common.dto.ad3.AD3OnlineAcquireDTO;
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
     * AD3线下BSC
     *
     * @param ad3CSBScanPayDTO   AD3线下BSC输入实体
     * @return BaseResponse
     */
    BaseResponse offlineBsc(AD3BSCScanPayDTO ad3CSBScanPayDTO);


    /**
     * @Author YangXu
     * @Date 2019/12/20
     * @Descripate AD3线下退款接口
     * @return
     **/
    BaseResponse offlineRefund(AD3ONOFFRefundDTO ad3RefundDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/20
     * @Descripate AD3线上退款接口
     **/
    BaseResponse onlineRefund(AD3ONOFFRefundDTO sendAdRefundDTO);

    /**
     * AD3 线上收款
     *
     * @param ad3OnlineAcquireDTO AD3线上收单接口参数实体
     * @return BaseResponse
     */
    BaseResponse onlinePay(AD3OnlineAcquireDTO ad3OnlineAcquireDTO);

    /**
     * @Author YangXu
     * @Date 2019/12/24
     * @Descripate AD3查询接口
     * @return
     **/
    BaseResponse query(AD3ONOFFRefundDTO ad3ONOFFRefundDTO);

}
