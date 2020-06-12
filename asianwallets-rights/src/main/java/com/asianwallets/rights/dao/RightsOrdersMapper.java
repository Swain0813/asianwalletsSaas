package com.asianwallets.rights.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.RightsOrdersDTO;
import com.asianwallets.common.dto.RightsOrdersExportDTO;
import com.asianwallets.common.dto.RightsOrdersOutDTO;
import com.asianwallets.common.entity.RightsOrders;
import com.asianwallets.common.vo.RightsOrdersApiVO;
import com.asianwallets.common.vo.RightsOrdersVO;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RightsOrdersMapper extends  BaseMapper<RightsOrders>{


    /**
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 权益核销分页查询
     * @return
     **/
    List<RightsOrdersVO> pageRightsOrders(RightsOrdersDTO rightsOrdersDTO);


    /**
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 导出权益核销
     * @return
     **/
    List<RightsOrdersVO> exportRightsOrders(RightsOrdersExportDTO rightsOrdersDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 权益核销查询API
     **/
    List<RightsOrdersApiVO> selectRightsOrders(RightsOrdersOutDTO rightsOrdersDTO);

    /**
     * 依据ticketId查询参数
     *
     * @param rightsOrdersDTO
     * @return
     */
    RightsOrders selectByTicketId(RightsOrdersDTO rightsOrdersDTO);
}
