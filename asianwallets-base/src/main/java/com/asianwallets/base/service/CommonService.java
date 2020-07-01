package com.asianwallets.base.service;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.MerchantCardCode;

/**
 * 共同模块
 */
public interface CommonService {

    /**
     * 从缓存里获取机构信息
     * @param institutionCode
     * @return
     */
    Institution getInstitutionInfo(String institutionCode);

    /**
     * 获取商户信息
     * @param merchantId
     * @return
     */
    Merchant getMerchant(String merchantId);

    /**
     * 根据通道id获取通道信息
     * @param channelId
     * @return
     */
    Channel getChannelById(String channelId);

    /**
     * 根据静态码编号获取静态码信息
     * @param id
     * @return
     */
    MerchantCardCode getMerchantCardCode(String id);
}
