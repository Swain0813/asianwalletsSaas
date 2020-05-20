package com.asianwallets.permissions.service;

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

}
