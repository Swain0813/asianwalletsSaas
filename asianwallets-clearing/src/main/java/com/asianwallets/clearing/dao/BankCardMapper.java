package com.asianwallets.clearing.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.BankCard;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 银行卡表mapper
 */
@Repository
public interface BankCardMapper extends BaseMapper<BankCard> {

    /**
     * 根据商户编号以及币种查询该商户启用并且默认的银行卡信息
     * @param merchantId
     * @param currency
     * @return
     */
    BankCard getBankCard(@Param("merchantId") String merchantId,@Param("currency") String currency);

}