package com.asianwallets.clearing.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.TcsCtFlow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TcsCtFlowMapper extends BaseMapper<TcsCtFlow> {


    /**
     * 结算处理前查询当前待结算数据还有多少可以处理的
     * 清算资金，主要用在订单有RV的情况下清算资金检查
     * @return
     */
    Double getCLLeftMoney(TcsCtFlow ctflow);

    /**
     * 根据机构编号以及币种查询未结算的收单清算记录
     * @param institutionCode
     * @param currency
     * @return
     */
    @Select("select sum(txnamount-fee+refundOrderFee) as unClearAmount from tcs_ctflow where merchantid = #{institutionCode} and txncurrency = #{currency} and CTstate=1")
    BigDecimal getUnClearAmount(@Param("institutionCode") String institutionCode, @Param("currency") String currency);

}
