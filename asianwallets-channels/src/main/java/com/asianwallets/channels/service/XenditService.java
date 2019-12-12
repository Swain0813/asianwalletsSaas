package com.asianwallets.channels.service;

import com.asianwallets.common.dto.xendit.XenditDTO;
import com.asianwallets.common.response.BaseResponse;

public interface XenditService {

    /**
     * xendit网银收单方法
     *
     * @param xenditDTO xendit请求实体
     * @return
     */
    BaseResponse xenditPay(XenditDTO xenditDTO);


    /**
     * 创建一个虚拟账户
     *
     * @param bankCode
     * @param apiKey
     * @return
     */
    BaseResponse creatVirtualAccounts(String bankCode, String apiKey, String bankName);


    /**
     * xendit可用银行查询接口
     *
     * @param apiKey  xendit可用银行查询接口
     * @param extend5
     * @return
     */
    BaseResponse xenditBanks(String apiKey, String extend5);

    /**
     * xendit根据OrderId查询订单信息
     *
     * @param OrderId
     * @param apiKey
     * @param payUrl
     * @return
     */
    BaseResponse getPayInfo(String OrderId, String apiKey, String payUrl);
}
