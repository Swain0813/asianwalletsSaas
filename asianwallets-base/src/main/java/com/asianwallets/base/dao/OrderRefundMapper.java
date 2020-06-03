package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.vo.OrdersRefundDetailVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

    /**
     * 查询对应渠道响应时间的订单
     * @param startTime
     * @param endTime
     * @param list
     * @return
     */
    List<OrderRefund> getYesterDayDate(@Param("startTime") Date startTime , @Param("endTime") Date endTime, @Param("list") List<String> list);

    /**
     * 退款补单操作
     * @param id
     * @param status
     * @param remark
     * @return
     */
    @Update("update order_refund set refund_status =#{status},remark1=#{remark},update_time=NOW() where id = #{id}")
    int supplementStatus(@Param("id")String id,@Param("status") Byte status,@Param("remark") String remark);
}