package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Attestation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttestationMapper extends BaseMapper<Attestation> {
    /**
     * 根据商户ID获取密钥对象
     *
     * @param merchantId 商户ID
     * @return 密钥对象
     */
    Attestation selectByMerchantId(@Param("merchantId") String merchantId);
}