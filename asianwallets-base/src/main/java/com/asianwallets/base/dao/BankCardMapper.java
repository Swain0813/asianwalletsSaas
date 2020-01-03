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
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户id查询银行卡
     **/
    List<BankCard> selectBankCardByMerId(@Param("merchantId") String merchantId);

    /**
     * 根据机构code和银行账号获取银行卡信息
     *
     * @param bankAccountCode
     * @return
     */
    @Select("SELECT id as id,merchant_id as merchantId,account_code as accountCode,bank_account_code as bankAccountCode,settle_currency as settleCurrency,bank_currency as bankCurrency FROM bank_card WHERE merchant_id = #{merchantId} and bank_account_code = #{bankAccountCode} and enabled=true")
    List<BankCard> getBankCards(@Param("merchantId") String merchantId, @Param("bankAccountCode") String bankAccountCode);


    /**
     * 根据银行卡id查询银行卡信息
     *
     * @param bankCardId
     * @return
     */
    @Select("SELECT id,merchant_id as merchantId,bank_account_code as bankAccountCode,settle_currency as settleCurrency,bank_currency as bankCurrency,enabled FROM bank_card WHERE id=#{bankCardId}")
    BankCard getBankCard(@Param("bankCardId") String bankCardId);

    /**
     * 根据商户编号查询默认的银行卡
     * @param merchantId
     * @return
     */
    @Select("SELECT id,merchant_id as merchantId,account_code as accountCode,bank_account_code as bankAccountCode,settle_currency as settleCurrency,bank_currency as bankCurrency FROM bank_card WHERE merchant_id = #{merchantId} and default_flag=true")
    List<BankCard> checkDefaultBankCard(@Param("merchantId") String merchantId);

    /**
     * 根据银行卡code查询启用的银行卡信息
     * @param bankAccountCode
     * @return
     */
    List<BankCard> selectBankCards(@Param("bankAccountCode") String bankAccountCode);


    /**
     * 根据商户编号以及币种查询该商户启用并且默认的银行卡信息
     * @param merchantId
     * @param currency
     * @return
     */
    BankCard getBankCard(@Param("merchantId") String merchantId,@Param("currency") String currency);

}
