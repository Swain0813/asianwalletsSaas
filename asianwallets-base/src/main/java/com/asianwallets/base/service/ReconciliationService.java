package com.asianwallets.base.service;
import com.asianwallets.common.dto.ReconOperDTO;
import com.asianwallets.common.dto.ReconciliationDTO;
import com.asianwallets.common.dto.SearchAvaBalDTO;
import com.asianwallets.common.entity.Reconciliation;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 调账功能
 */
public interface ReconciliationService {


    /**
     * 分页查询调账单
     * @param reconciliationDTO
     * @return
     */
    PageInfo<Reconciliation> pageReconciliation(ReconciliationDTO reconciliationDTO);

    /**
     * 分页审核调账单
     * @param reconciliationDTO
     * @return
     */
    PageInfo<Reconciliation> pageReviewReconciliation(ReconciliationDTO reconciliationDTO);


    /**
     * 资金变动操作
     * @param name
     * @param reconOperDTO
     * @return
     */
    String doReconciliation(String name, ReconOperDTO reconOperDTO);

    /**
     * 资金变动审核
     * @param name
     * @param reconciliationId
     * @param enabled
     * @param remark
     * @return
     */
    String auditReconciliation(String name, String reconciliationId, boolean enabled, String remark);


    /**
     * 查询可用余额
     *
     * @param searchAvaBalDTO
     * @return
     */
    String getAvailableBalance(SearchAvaBalDTO searchAvaBalDTO);

}
