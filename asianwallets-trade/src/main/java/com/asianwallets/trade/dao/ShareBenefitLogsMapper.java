package com.asianwallets.trade.dao;

import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.entity.ShareBenefitLogs;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
  * 分润流水表 Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2020-01-03
 */
@Repository
public interface ShareBenefitLogsMapper extends  BaseMapper<ShareBenefitLogs> {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/8/23
     * @Descripate 根据订单号查询流水是否村咋
     **/
    @Select("select count(1) from share_benefit_logs where order_id = #{orderId} and agent_type =#{agentType}")
    int selectCountByOrderId(@Param("orderId") String orderId , @Param("agentType") String agentType);

    List<ShareBenefitLogs> selectByOrderId(@Param("orderId") String orderId);

}
