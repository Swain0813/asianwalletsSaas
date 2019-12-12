package com.asianwallets.clearing.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Merchant;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantMapper extends BaseMapper<Merchant> {


    /**
     * 根据商户code获取商户信息
     * @param merchantId
     * @return
     */
    Merchant selectByMerchantId(@Param("merchantId") String merchantId);
}