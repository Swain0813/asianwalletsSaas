package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.BankIssuerId;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 银行 - IssuerID
 */
@Repository
public interface BankIssuerIdMapper extends BaseMapper<BankIssuerId> {

    /**
     * 通过通道币种,银行名称, ,查找映射表
     *
     * @param Currency 通道币种
     * @param bankName 银行名称
     * @param channelCode 通道Code
     * @return  BankIssuerId
     */
    BankIssuerId selectBankAndIssuerId(@Param("bankCurrency") String Currency, @Param("bankName") String bankName, @Param("channelCode") String channelCode);

}