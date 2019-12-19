package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.vo.OrdersRefundDetailVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 退款
 */
@Repository
public interface OrderRefundMapper extends BaseMapper<OrderRefund> {

    /**
     * @Author YangXu
     * @Date 2019/2/19
     * @Descripate 根据原订单id查询退款总金额
     * @return
     **/
    @Select("select sum(amount) from order_refund where order_id = #{orderId} and refund_status != 3")
    BigDecimal getTotalAmountByOrderId(@Param("orderId") String orderId);

    /**
     * 人工退款失败的场合更新退款单信息
     * @param id
     * @param remark
     * @return
     */
    @Update("update order_refund set refund_status =#{status},remark =#{remark},update_time= NOW() where id = #{id} and refund_status=1")
    int updaterefundOrder(@Param("id") String id,@Param("status") Byte status,@Param("remark") String remark);

    /**
     * @Author YangXu
     * @Date 2019/2/28
     * @Descripate 更新退款状态
     * @return
     **/
    @Update("update order_refund set refund_status =#{status},refund_channel_number = #{txnId},update_time= NOW(),remark =#{remark} where id = #{merOrderNo}")
    int updateStatuts(@Param("merOrderNo") String merOrderNo, @Param("status") Byte status, @Param("txnId") String txnId,@Param("remark") String remark);
}
