package com.asianwallets.trade.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.TcsCtFlow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TcsCtFlowMapper extends BaseMapper<TcsCtFlow> {

    /**
     * @Author YangXu
     * @Date 2019/3/13
     * @Descripate 根据系统订单号查询清算状态:1-待清算，2-已清算
     * @return
     **/
    @Select("select CTstate from tcs_ctflow where sysorderid = #{sysorderid} and refcnceFlow = #{sysorderid}  and tradetype = 'NT'")
    Integer getCTstatus(String sysorderid);

    /**
     * 根据机构编号以及币种查询未清算记录
     * @param institutionCode
     * @param currency
     * @return
     */
    @Select("select sum(txnamount-fee+refundOrderFee) as unClearAmount from tcs_ctflow where merchantid = #{institutionCode} and txncurrency = #{currency} and CTstate=1")
    BigDecimal getUnClearAmount(@Param("institutionCode") String institutionCode, @Param("currency") String currency);

}
