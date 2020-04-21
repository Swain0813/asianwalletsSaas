package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.trade.vo.OnlineMerchantVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface MerchantMapper extends BaseMapper<Merchant> {

    /**
     * 线上查询信息
     *
     * @param merchantId
     * @param payType
     * @param tradeDirection
     * @param language
     * @return
     */
    OnlineMerchantVO selectRelevantInfo(@Param("merchantId") String merchantId, @Param("payType") String payType, @Param("tradeDirection") Byte tradeDirection, @Param("language") String language);

    /**
     * 线上查询信息 无bank列表
     *
     * @param merchantId
     * @param payType
     * @param tradeDirection
     * @param language
     * @return
     */
    OnlineMerchantVO selectRelevantInfoNoBank(@Param("merchantId") String merchantId, @Param("payType") String payType, @Param("tradeDirection") Byte tradeDirection, @Param("language") String language);
}
