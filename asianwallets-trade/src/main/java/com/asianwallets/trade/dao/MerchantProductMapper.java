package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.MerchantProductDTO;
import com.asianwallets.common.entity.MerchantProduct;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantProductMapper extends BaseMapper<MerchantProduct> {

    /**
     * 根据商户ID与产品ID查询商户产品
     *
     * @param merchantId 商户ID
     * @param productId  产品ID
     * @return 商户产品
     */
    MerchantProduct selectByMerchantIdAndProductId(@Param("merchantId") String merchantId, @Param("productId") String productId);
}
