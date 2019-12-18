package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.TcsStFlow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface TcsStFlowMapper extends BaseMapper<TcsStFlow> {


    /**
     * @Author YangXu
     * @Date 2019/3/13
     * @Descripate 根据系统订单号查询结算状态: 1-未结算，2-已结算
     * @return
     **/
    @Select("select STstate from tcs_stflow where sysorderid = #{sysorderid} and refcnceFlow = #{sysorderid} and tradetype = 'ST'")
    Integer getSTstatus(@Param("sysorderid") String sysorderid);
    /**
     * @Author YangXu
     * @Date 2019/12/12
     * @Descripate 查询未结算的金额
     * @return
     **/
    @Select("select sum(txnamount-fee+refundOrderFee) as unSettleAmount from tcs_stflow where merchantid = #{merchantid} and txncurrency = #{txncurrency} and STstate=1")
    BigDecimal getUnSettleAmount(@Param("merchantid") String merchantid, @Param("txncurrency") String txncurrency);
}
