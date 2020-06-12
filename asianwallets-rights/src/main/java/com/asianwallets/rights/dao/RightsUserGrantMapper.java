package com.asianwallets.rights.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.RightsUserGrant;
import com.asianwallets.common.vo.RightsUserGrantDetailVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RightsUserGrantMapper extends BaseMapper<RightsUserGrant> {

    /**
     * 根据票券编号获取票券信息
     * @param ticketId
     * @return
     */
    RightsUserGrant selectByTicketId(@Param("ticketId") String ticketId);


    /**
     * 根据多个票券id对应的票券信息
     * @param ticketId
     * @return
     */
    List<RightsUserGrant> selectByTicketIds(List<String> ticketId);

    /**
     * 核销修改票券状态
     *
     * @param id
     * @param ticketStatus
     * @return
     */
    @Update("update rights_user_grant set ticket_status = #{ticketStatus},cancel_verification_time = NOW(),update_time = NOW() where ticket_status = 2 and id = #{id}")
    int updateTicketStatus(@Param("id") String id, @Param("ticketStatus") Byte ticketStatus);


    /**
     * 退款修改票券状态
     *
     * @param id
     * @param ticketStatus
     * @return
     */
    @Update("update rights_user_grant set ticket_status = #{ticketStatus},cancel_verification_time = NOW(),update_time = NOW() where ticket_status in (2,4) and id = #{id}")
    int updateTicketStatusRefund(@Param("id") String id, @Param("ticketStatus") Byte ticketStatus);

    /**
     * 查询权益票券详情
     *
     * @param ticketId 票券编号
     * @return
     */
    RightsUserGrantDetailVO getRightsUserGrantDetail(String ticketId);
}
