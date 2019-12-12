package com.asianwallets.channels.service;

import com.asianwallets.common.dto.alipay.*;
import com.asianwallets.common.response.BaseResponse;
import org.apache.commons.httpclient.NameValuePair;

import java.util.Map;

public interface AliPayService {

    /**
     * AliPay线下BSC收单方法
     *
     * @param aliPayOfflineBSCDTO aliPay线下BSC实体
     * @return
     */
    BaseResponse aliPayOfflineBSC(AliPayOfflineBSCDTO aliPayOfflineBSCDTO);

    /**
     * AliPay线下CSB收单方法
     *
     * @param aliPayCSBDTO aliPay线下CSB实体
     * @return
     */
    BaseResponse aliPayCSB(AliPayCSBDTO aliPayCSBDTO);

    /**
     * 支付宝CBAlipayWebsite接口
     *
     * @param aliPayWebDTO 支付宝CBAlipayWebsite接口
     * @return
     */
    BaseResponse aliPayWebsite(AliPayWebDTO aliPayWebDTO);

    /**
     * aliPay查询
     *
     * @param orderId                :订单id
     * @param channelSingleSelectUrl
     * @return
     */
    Map<String, String> aliPayQueryOrder(String orderId, String channelSingleSelectUrl);

    /**
     * aliPay撤销
     *
     * @param aliPayCancelDTO 撤销实体
     * @return
     */
    BaseResponse alipayCancel(AliPayCancelDTO aliPayCancelDTO);

    /**
     * aliPay退款
     *
     * @param aliPayRefundDTO 退款实体
     * @return
     */
    BaseResponse aliPayRefund(AliPayRefundDTO aliPayRefundDTO);

    /**
     * aliPay查询
     *
     * @param aliPayQueryDTO 查询实体
     * @return
     */
    BaseResponse aliPayQuery(AliPayQueryDTO aliPayQueryDTO);


    NameValuePair[] CreateAlipayHttpPostParams(Map<String, String> signMap);
}
