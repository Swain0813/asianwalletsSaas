package com.asianwallets.base.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.PreOrdersDTO;
import com.asianwallets.common.entity.PreOrders;
import com.asianwallets.common.vo.ExportPreOrdersVO;
import com.asianwallets.common.vo.PreOrdersVO;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 预授权订单表
 */
@Repository
public interface PreOrdersMapper extends BaseMapper<PreOrders> {
    /**
     * 分页查询预授权订单信息
     * @param preOrdersDTO
     * @return
     */
    List<PreOrders> pageFindPreOrders(PreOrdersDTO preOrdersDTO);

    /**
     * 查询预授权订单详情信息
     * @param preOrdersDTO
     * @return
     */
    PreOrdersVO getPreOrdersDetail(PreOrdersDTO preOrdersDTO);

    /**
     * 预授权订单导出
     * @param preOrdersDTO
     * @return
     */
    List<ExportPreOrdersVO> exportPreOrders(PreOrdersDTO preOrdersDTO);

}