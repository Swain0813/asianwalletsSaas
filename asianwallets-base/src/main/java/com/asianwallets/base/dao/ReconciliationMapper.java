package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.ReconciliationDTO;
import com.asianwallets.common.dto.SearchAvaBalDTO;
import com.asianwallets.common.entity.Reconciliation;
import com.asianwallets.common.vo.FrozenMarginInfoVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 调账的数据层
 */
@Repository
public interface ReconciliationMapper extends BaseMapper<Reconciliation> {

    /**
     * 根据id修改调账单状态
     * @param id
     * @param status
     * @param name
     * @param remark1
     * @return
     */
    @Update("update reconciliation set status = #{status} ,remark1 = #{remark1},update_time=NOW(),modifier=#{name} where id = #{id}")
    int updateStatusById(@Param("id") String id, @Param("status") int status, @Param("name") String name, @Param("remark1") String remark1);

    /**
     * 分页查询调账单
     * @param reconciliationDTO
     * @return
     */
    List<Reconciliation> pageReconciliation(ReconciliationDTO reconciliationDTO);

    /**
     * 分页查询审核调账单
     * @param reconciliationDTO
     * @return
     */
    List<Reconciliation> pageReviewReconciliation(ReconciliationDTO reconciliationDTO);


    /**
     * 查询冻结金额
     *
     * @param searchAvaBalDTO
     * @return
     */
    BigDecimal selectFreezeBalance(SearchAvaBalDTO searchAvaBalDTO);


    /**
     * 查询可用余额
     *
     * @param searchAvaBalDTO
     * @return
     */
    BigDecimal selectAvailableBalance(SearchAvaBalDTO searchAvaBalDTO);

    /**
     *查询冻结成功和解冻成功的调账记录
     * @param frozenMarginInfoDTO
     * @return
     */
    List<FrozenMarginInfoVO> pageFrozenLogs(FrozenMarginInfoDTO frozenMarginInfoDTO);
}
