package com.asianwallets.rights.service;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.entity.Merchant;

/**
 * 共通从redis获取基础数据
 */
public interface CommonRedisService {


    /**
     * 根据机构编号获取机构信息
     *
     * @param institutionCode 机构号
     * @return Institution
     */
    Institution getInstitutionInfo(String institutionCode);

    /**
     * 获取机构的公钥
     *
     * @param merchantId 商户号
     * @return Attestation
     */
    Attestation getAttestationInfo(String merchantId);

    /**
     * 获取商户信息
     * @param merchantId
     * @return
     */
    Merchant getMerchant(String merchantId);

}
