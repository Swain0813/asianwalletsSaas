package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.MerchantProduct;
import com.asianwallets.trade.vo.OnlineInfoDetailVO;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 获取线上商户信息
     *
     * @param merchantId 商户ID
     * @param issuerId   issuerID
     * @return OnlineInfoVO
     */
    List<OnlineInfoDetailVO> selectOnlineInfo(@Param("merchantId") String merchantId, @Param("issuerId") String issuerId);
}
