package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.ReconOperDTO;
import com.asianwallets.common.dto.ReconciliationDTO;
import com.asianwallets.common.dto.SearchAvaBalDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.ReconciliationFeign;
import org.springframework.stereotype.Component;
import javax.validation.Valid;

/**
 * 调账模块fegin的实现类
 */
@Component
public class ReconciliationFeignImpl implements ReconciliationFeign {

    /**
     * 分页查询调账单
     * @param reconciliationDTO
     * @return
     */
    @Override
    public BaseResponse pageReconciliation(ReconciliationDTO reconciliationDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 分页查询审核调账单
     * @param reconciliationDTO
     * @return
     */
    @Override
    public BaseResponse pageReviewReconciliation(ReconciliationDTO reconciliationDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 资金变动审核
     * @param reconciliationId
     * @param enabled
     * @param remark
     * @return
     */
    @Override
    public BaseResponse auditReconciliation(String reconciliationId, boolean enabled, String remark) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 资金变动操作
     * @param reconOperDTO
     * @return
     */
    @Override
    public BaseResponse doReconciliation(ReconOperDTO reconOperDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 查询可用余额
     * @param searchAvaBalDTO
     * @return
     */
    @Override
    public BaseResponse getAvailableBalance(@Valid SearchAvaBalDTO searchAvaBalDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
