package com.asianwallets.base.service;

import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.Merchant;

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
}
