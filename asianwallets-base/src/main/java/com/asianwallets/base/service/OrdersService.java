package com.asianwallets.base.service;
import com.asianwallets.common.dto.DccReportDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.vo.DccReportVO;
import com.asianwallets.common.vo.ExportOrdersVO;
import com.asianwallets.common.vo.OrdersDetailVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface OrdersService {

    /**
     * 分页查询订单信息
     *
     * @param ordersDTO 订单输入实体
     * @return 订单集合
     */
    PageInfo<Orders> pageFindOrders(OrdersDTO ordersDTO);

    /**
     * 查询订单详情信息
     *
     * @param id 订单id
     * @return 订单详情输出实体
     */
    OrdersDetailVO getOrdersDetail(String id);

    /**
     * 订单导出
     *
     * @param ordersDTO 订单实体
     * @return 订单集合
     */
    List<ExportOrdersVO> exportOrders(OrdersDTO ordersDTO);

    /**
     *DCC报表查询
     * @param dccReportDTO
     * @return
     */
    PageInfo<DccReportVO> getDccReport(DccReportDTO dccReportDTO);
}
