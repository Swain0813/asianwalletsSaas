package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.InsDailyTradeMapper;
import com.asianwallets.base.dao.OrdersMapper;
import com.asianwallets.base.service.OrdersService;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.DccReportDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.InsDailyTrade;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.vo.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private InsDailyTradeMapper insDailyTradeMapper;

    @Autowired
    private AuditorProvider auditorProvider;

    /**
     * 分页查询订单信息
     *
     * @param ordersDTO 订单输入实体
     * @return 订单集合
     */
    @Override
    public PageInfo<Orders> pageFindOrders(OrdersDTO ordersDTO) {
        ordersDTO.setLanguage(auditorProvider.getLanguage());
        return new PageInfo<>(ordersMapper.pageFindOrders(ordersDTO));
    }

    /**
     * 查询订单详情信息
     *
     * @param id 订单id
     * @return 订单详情输出实体
     */
    @Override
    public OrdersDetailVO getOrdersDetail(String id) {
        OrdersDetailVO ordersDetailVO = ordersMapper.selectOrdersDetailById(id, auditorProvider.getLanguage());
        if (TradeConstant.EN_US.equals(auditorProvider.getLanguage())) {
            if (TradeConstant.FEE_TYPE_RATE.equals(ordersDetailVO.getRateType())) {
                ordersDetailVO.setRateType("Single Rate");
            } else {
                ordersDetailVO.setRateType("Single Quota");
            }
        } else {
            if (TradeConstant.FEE_TYPE_RATE.equals(ordersDetailVO.getRateType())) {
                ordersDetailVO.setRateType("单笔费率");
            } else {
                ordersDetailVO.setRateType("单笔定额");
            }
        }
        List<OrdersDetailRefundVO> ordersDetailRefundVOS = ordersDetailVO.getOrdersDetailRefundVOS();
        List<OrdersDetailRefundVO> collect = ordersDetailRefundVOS.stream().sorted(Comparator.comparing(OrdersDetailRefundVO::getOrderRefundCreateTime).reversed()).collect(Collectors.toList());
        ordersDetailVO.setOrdersDetailRefundVOS(collect);
        return ordersDetailVO;
    }

    /**
     * 订单导出
     *
     * @param ordersDTO 订单实体
     * @return 订单集合
     */
    @Override
    public List<ExportOrdersVO> exportOrders(OrdersDTO ordersDTO) {
        ordersDTO.setLanguage(auditorProvider.getLanguage());
        return ordersMapper.exportOrders(ordersDTO);
    }

    /**
     * DCC报表查询
     *
     * @param dccReportDTO
     * @return
     */
    @Override
    public PageInfo<DccReportVO> getDccReport(DccReportDTO dccReportDTO) {
        List<DccReportVO> dccReportList = ordersMapper.pageDccReport(dccReportDTO);
        if (dccReportList != null && dccReportList.size() != 0) {
            for (DccReportVO dccReportVO : dccReportList) {
                if (!StringUtils.isEmpty(dccReportVO.getOrderForTradeRate())) {
                    //浮动金额=交易金额-订单金额*原始汇率
                    dccReportVO.setFloatAmount(dccReportVO.getTradeAmount().subtract(dccReportVO.getOrderAmount().multiply(dccReportVO.getOrderForTradeRate())).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        }
        return new PageInfo<>(dccReportList);
    }

    /**
     * DCC报表导出
     *
     * @param dccReportExportDTO
     * @return
     */
    @Override
    public List<DccReportVO> exportDccReport(DccReportDTO dccReportExportDTO) {
        List<DccReportVO> dccReportList = ordersMapper.pageDccReport(dccReportExportDTO);
        if (dccReportList != null && dccReportList.size() != 0) {
            for (DccReportVO dccReportVO : dccReportList) {
                if (!StringUtils.isEmpty(dccReportVO.getOrderForTradeRate())) {
                    //浮动金额=交易金额-订单金额*原始汇率
                    dccReportVO.setFloatAmount(dccReportVO.getTradeAmount().subtract(dccReportVO.getOrderAmount().multiply(dccReportVO.getOrderForTradeRate()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                }
            }
        }
        return dccReportList;
    }

    /**
     * 分页查询机构日交易汇总表
     *
     * @param ordersDTO 订单实体
     * @return 订单集合
     */
    @Override
    public PageInfo<InsDailyTrade> pageFindInsDailyTrade(OrdersDTO ordersDTO) {
        return new PageInfo<>(insDailyTradeMapper.pageFindInsDailyTrade(ordersDTO));
    }

    /**
     * 导出机构日交易汇总表
     *
     * @param ordersDTO 订单实体
     * @return 订单集合
     */
    @Override
    public List<InsDailyTradeVO> exportInsDailyTrade(OrdersDTO ordersDTO) {
        return insDailyTradeMapper.exportInsDailyTrade(ordersDTO);
    }
}
