package com.asianwallets.base.dao;

import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.entity.Account;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

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
     * 根据机构code和币种获取账户信息
     * @param institutionId
     * @param currency
     * @return
     */
    @Select("select account_code from account where institution_id = #{institutionId} and settle_currency = #{currency}")
    String getAccountCode(@Param("institutionId") String institutionId, @Param("currency") String currency);

    /**
     * @Author YangXu
     * @Date 2019/1/29
     * @Descripate 根据币种和机构id查询数量
     * @return
     **/
    @Select("select count(1) from account where merchant_id = #{merchantId} and currency = #{currency}")
    int getCountByinstitutionIdAndCurry(@Param("merchantId") String merchantId, @Param("currency") String currency);
}
