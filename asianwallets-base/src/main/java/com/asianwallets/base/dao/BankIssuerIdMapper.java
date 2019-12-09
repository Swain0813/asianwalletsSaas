package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.BankIssuerId;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BankIssuerIdMapper extends BaseMapper<BankIssuerId> {

    /**
     * 根据银行名称与银行币种查询银行与银行机构代码映射关系
     *
     * @param bankName 银行名称
     * @param currency 银行币种
     * @return 银行与银行机构代码映射集合
     */
    List<BankIssuerId> selectByBankName(@Param("bankName") String bankName, @Param("currency") String currency);
}