package com.asianwallets.base.dao;

import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.dto.BankCardSearchDTO;
import com.asianwallets.common.entity.BankCard;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
  * 银行卡表 Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Repository
public interface BankCardMapper extends  BaseMapper<BankCard> {


    /**
     * @return
     * @Author YangXu
     * @Date 2019/2/27
     * @Descripate 分页查询银行卡
     **/
    List<BankCard> pageBankCard(BankCardSearchDTO bankCardSearchDTO);


    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户id查询银行卡
     * @return
     **/
    List<BankCard> selectBankCardByMerId(@Param("merchantId") String merchantId);

    /**
     * 根据机构code和银行账号获取银行卡信息
     * @param bankAccountCode
     * @return
     */
    @Select("SELECT id as id,merchant_id as merchantId,account_code as accountCode,bank_account_code as bankAccountCode,settle_currency as settleCurrency,bank_currency as bankCurrency FROM bank_card WHERE merchant_id = #{merchantId} and bank_account_code = #{bankAccountCode} and enabled=true")
    List<BankCard> getBankCards(@Param("merchantId") String merchantId, @Param("bankAccountCode") String bankAccountCode);

    /**
     * 根据机构code，银行卡币种以及结算币种和是否设为默认银行卡查询银行卡信息
     * @return
     */
    @Select("SELECT id,merchant_id as merchantId,settle_currency as settleCurrency,bank_currency as bankCurrency,bank_account_code as bankAccountCode from bank_card WHERE merchant_id = #{merchantId} and settle_currency = #{settleCurrency} and default_flag=true")
    List<BankCard> selectUpdateBankCard(@Param("merchantId") String merchantId, @Param("settleCurrency") String settleCurrency);
}
