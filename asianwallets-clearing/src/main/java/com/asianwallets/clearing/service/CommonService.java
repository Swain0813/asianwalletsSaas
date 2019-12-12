package com.asianwallets.clearing.service;
import com.asianwallets.common.entity.Merchant;

/**
 * 共通方法
 */
public interface CommonService {
    /**
     * 获得商户信息从redis里获取
     *
     * @param merchantId
     * @return
     */
    Merchant getMerchantInfo(String merchantId);

}
