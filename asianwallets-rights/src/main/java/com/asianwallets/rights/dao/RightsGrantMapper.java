package com.asianwallets.rights.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.RightsGrantDTO;
import com.asianwallets.common.entity.RightsGrant;
import com.asianwallets.common.entity.RightsUserGrant;
import com.asianwallets.common.vo.ExportRightsGrantVO;
import com.asianwallets.common.vo.ExportRightsUserGrantVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RightsGrantMapper extends BaseMapper<RightsGrant> {


    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 判断票券编号是否存在
     **/
    RightsGrant selectByTicketId(@Param("ticketId") String ticketId);

    /**
     * 分页查询权益发放管理信息
     *
     * @param rightsGrantDTO
     * @return
     */
    List<RightsGrant> pageFindRightsGrant(RightsGrantDTO rightsGrantDTO);

    /**
     * 查询权益发放管理信息详情
     * @param rightsGrantDTO
     * @return
     */
    RightsGrant selectRightsGrantInfo(RightsGrantDTO rightsGrantDTO);

    /**
     * 导出权益发放信息
     *
     * @param rightsGrantDTO
     * @return
     */
    List<ExportRightsGrantVO> exportRightsGrants(RightsGrantDTO rightsGrantDTO);


    /**
     * 根据团购号查询权益
     *
     * @param dealId 团购号
     * @return 权益
     */
    RightsGrant selectByDealId(String dealId);

    /**
     * 分页查询权益票券信息
     *
     * @param rightsGrantDTO
     * @return
     */
    List<RightsUserGrant> pageFindRightsUserGrant(RightsGrantDTO rightsGrantDTO);

    /**
     * @Author YangXu
     * @Date 2020/1/3
     * @Descripate 核销成功更新核销数量
     * @return
     **/
    @Update("update rights_grant set cancel_verification_amount = cancel_verification_amount+1 ,update_time = NOW(),cancel_verification_time = NOW(),ext1 = '核销成功更新核销数量' where deal_id =#{dealId}")
    int updateCancelVerificationAmount(@Param("dealId") String dealId);

    /**
     * @Author YangXu
     * @Date 2020/1/3
     * @Descripate 退款成功更新剩余数量
     * @return
     **/
    @Update("update rights_grant set surplus_amount = surplus_amount+1 ,update_time = NOW() ,ext2 = '退款成功更新剩余数量'where deal_id =#{dealId}")
    int updateSurplusAmount(@Param("dealId") String dealId);

    /**
     * 导出权益票券信息
     *
     * @param rightsGrantDTO 输入DTO
     * @return
     */
    List<ExportRightsUserGrantVO> exportRightsUserGrant(RightsGrantDTO rightsGrantDTO);


    /**
     * 根据机构权益订单号查询是不是已经上送了
     * @param batchNo
     * @return
     */
    RightsGrant selectByBatchNo(String batchNo);
}
