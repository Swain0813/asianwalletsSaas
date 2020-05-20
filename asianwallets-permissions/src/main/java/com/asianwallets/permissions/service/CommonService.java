package com.asianwallets.permissions.service;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.Merchant;
/**
 * 权限服务的共通业务
 */
public interface CommonService {

    /**
     * 获取商户信息
     * @param merchantId
     * @return
     */
    Merchant getMerchant(String merchantId);

    /**
     * 从缓存里获取机构信息
     * @param institutionCode
     * @return
     */
    Institution getInstitutionInfo(String institutionCode);

    /**
     * 根据通道编号获取通道信息
     *
     * @param channelCode 通道code
     */
    Channel getChannelByChannelCode(String channelCode);

}
