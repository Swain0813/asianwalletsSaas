package com.asianwallets.task.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Account;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 账户信息
 */
@Repository
public interface AccountMapper extends  BaseMapper<Account> {
    /**
     * 根据账户id获取账户信息
     * @param accountId
     * @return
     */
    @Select("select id,account_code as accountCode,merchant_id as merchantId,settle_balance as settleBalance,freeze_balance as freezeBalance,enabled as enabled from account where id = #{accountId} and enabled=1")
    Account getAccount(String accountId);
}
