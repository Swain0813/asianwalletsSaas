package com.asianwallets.clearing.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.TcsStFlow;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface TcsStFlowMapper extends BaseMapper<TcsStFlow> {


    /**
     * @Author YangXu
     * @Date 2019/7/29
     * @Descripate 查询所有date时间之前的未结算订单
     * @return
     **/
    List<TcsStFlow> selectList( TcsStFlow tcsStFlow );


    /**
     * @Author YangXu
     * @Date 2019/12/12
     * @Descripate 查询未结算的金额
     * @return
     **/
    @Select("select sum(txnamount-fee+refundOrderFee) as unSettleAmount from tcs_stflow where merchantid = #{merchantid} and txncurrency = #{currency} and STstate=1")
    BigDecimal getUnSettleAmount(String merchantid, String txncurrency);
}
