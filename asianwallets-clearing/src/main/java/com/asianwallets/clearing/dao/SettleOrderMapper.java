package com.asianwallets.clearing.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.SettleOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 结算交易Mapper
 */
@Repository
public interface SettleOrderMapper extends BaseMapper<SettleOrder> {

    /**
     * 根据商户编号以及银行卡卡号获取结算交易
     * @param merchantId
     * @param bankCode
     * @return
     */
    String getBatchNo(@Param("merchantId") String merchantId, @Param("bankCode") String bankCode);
}