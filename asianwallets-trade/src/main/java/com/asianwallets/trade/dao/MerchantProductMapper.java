package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.MerchantProduct;
import com.asianwallets.trade.vo.OnlineInfoDetailVO;
import com.asianwallets.trade.vo.PosMerProVO;
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
    List<OnlineInfoDetailVO> selectOnlineInfoDetail(@Param("merchantId") String merchantId, @Param("issuerId") String issuerId, @Param("tradeDirection") Byte tradeDirection);

    /**
     * 根据商户ID,交易方向,交易类型查询商户产品信息
     *
     * @param merchantId     商户ID
     * @param tradeDirection 交易方向
     * @param language       语言
     * @param tradeType      交易类型
     * @return 商户产品信息集合
     */
    List<PosMerProVO> selectMerPro(@Param("merchantId") String merchantId, @Param("tradeDirection") Byte tradeDirection, @Param("language") String language, @Param("tradeType") Byte tradeType);
}
