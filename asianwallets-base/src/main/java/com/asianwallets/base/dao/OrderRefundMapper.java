package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.vo.OrdersRefundDetailVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 退款
 */
@Repository
public interface OrderRefundMapper extends BaseMapper<OrderRefund> {

    /**
     * 分页查询退款订单
     *
     * @param ordersRefundDTO
     * @return List<OrdersRefundVO> @link OrdersRefundVO
     */
    List<OrdersRefundVO> pageFindOrdersRefund(OrdersRefundDTO ordersRefundDTO);

    /**
     * 查询退款订单详情信息
     *
     * @param refundId 退款订单ID
     * @param language 语言
     * @return OrdersRefundDetailVO
     */
    OrdersRefundDetailVO selectOrdersRefundDetailById(@Param("refundId") String refundId, @Param("language") String language);

    /**
     * 根据日期查询退款订单信息
     *
     * @param yesterday 昨日日期
     * @return
     */
    List<OrderRefund> selectByDate(String yesterday);
}