package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.vo.OrdersRefundDetailVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
}
