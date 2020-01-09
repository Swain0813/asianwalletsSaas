package com.asianwallets.base.dao;

import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.dto.AccountSearchExportDTO;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.vo.AccountListVO;
import com.asianwallets.common.vo.WithdrawalVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
  * 账户表 Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Repository
public interface AccountMapper extends  BaseMapper<Account> {

    /**
     * 分页查询账户信息
     * @param accountSearchDTO
     * @return
     */
    List<AccountListVO> pageFindAccount(AccountSearchDTO accountSearchDTO);

    /**
     * 导出账户信息
     * @param accountSearchDTO
     * @return
     */
    List<AccountListVO> exportAccountList(AccountSearchExportDTO accountSearchDTO);

    /**
     * @Author YangXu
     * @Date 2019/1/29
     * @Descripate 根据币种和机构id查询数量
     * @return
     **/
    @Select("select count(1) from account where merchant_id = #{merchantId} and currency = #{currency}")
    int getCountByinstitutionIdAndCurry(@Param("merchantId") String merchantId, @Param("currency") String currency);

    /**
     * 获取账户信息
     * @param merchantId
     * @param currency
     * @return
     */
    @Select("select id,account_code as accountCode,merchant_id as merchantId,settle_balance as settleBalance,freeze_balance as freezeBalance,enabled as enabled from account where merchant_id = #{merchantId} and currency = #{currency}")
    Account getAccount(@Param("merchantId") String merchantId, @Param("currency") String currency);


    /**
     * 查询账户表中结算账户余额减去冻结金额大于最小起结金额的账户信息
     *
     * @return
     * @param merchantId
     * @param currency
     */
    WithdrawalVO getAccountByWithdrawal(@Param("merchantId") String merchantId, @Param("currency") String currency);
}
