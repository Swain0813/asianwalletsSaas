package com.asianwallets.base.dao;

import com.asianwallets.base.vo.CheckAccountVO;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.DccReportDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 根据设备编号查询订单
     *
     * @param imei 设备号
     * @return 订单
     */
    Orders selectByImei(String imei);

    /**
     * 分页查询订单信息
     *
     * @param ordersAllDTO 订单输入实体
     * @return 订单集合
     */
    List<Orders> pageFindOrders(OrdersDTO ordersAllDTO);

    /**
     * 查询订单详情信息
     *
     * @param id 订单id
     * @return 订单详情输出实体
     */
    OrdersDetailVO selectOrdersDetailById(@Param("id") String id, @Param("language") String language);

    /**
     * 订单导出
     *
     * @param ordersDTO 订单实体
     * @return 订单集合
     */
    List<ExportOrdersVO> exportOrders(OrdersDTO ordersDTO);

    /**
     * DCC报表查询
     *
     * @param dccReportDTO
     * @return
     */
    List<DccReportVO> pageDccReport(DccReportDTO dccReportDTO);

    /**
     * 根据日期查询订单信息
     *
     * @param yesterday 昨日日期
     * @return
     */
    List<Orders> selectByDate(String yesterday);

    /**
     * 商户交易对账单
     *
     * @param yesterday 昨日日期
     * @return
     */
    List<CheckAccountVO> tradeAccountCheck(String yesterday);

    /**
     * 交易统计
     *
     * @param ordersDTO 订单实体
     * @return 订单统计信息
     */
    List<OrdersStatisticsVO> statistics(OrdersDTO ordersDTO);

    /**
     * 产品交易统计
     *
     * @param ordersDTO 订单实体
     * @return 订单统计信息
     */
    List<OrdersProStatisticsVO> productStatistics(OrdersDTO ordersDTO);

    /**
     *查询对应渠道响应时间的订单
     * @param startTime
     * @param endTime
     * @param list
     * @return
     */
    List<Orders> getYesterDayDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("list") List<String> list);

    /**
     * 补单操作
     * @param id
     * @param status
     * @param remark
     * @return
     */
    @Update("update orders set trade_status =#{status},remark1=#{remark},update_time=NOW() where id = #{id}")
    int supplementStatus(@Param("id") String id, @Param("status") Byte status, @Param("remark") String remark);
}