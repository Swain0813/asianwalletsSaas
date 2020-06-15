package com.asianwallets.base.service;
import com.asianwallets.common.dto.PreOrdersDTO;
import com.asianwallets.common.entity.PreOrders;
import com.asianwallets.common.vo.ExportPreOrdersVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface PreOrdersService {

    /**
     * 分页查询预授权订单信息
     * @param preOrdersDTO
     * @return
     */
    PageInfo<PreOrders> pageFindPreOrders(PreOrdersDTO preOrdersDTO);


    /**
     * 查询预授权订单详情信息
     * @param preOrdersDTO
     * @return
     */
    PreOrders getPreOrdersDetail(PreOrdersDTO preOrdersDTO);

    /**
     * 预授权订单导出
     * @param preOrdersDTO
     * @return
     */
    List<ExportPreOrdersVO> exportPreOrders(PreOrdersDTO preOrdersDTO);
}
