package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.ExportAgencyShareBenefitDTO;
import com.asianwallets.common.dto.QueryAgencyShareBenefitDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.QueryAgencyShareBenefitVO;
import com.asianwallets.permissions.feign.base.ShareBenefitFeign;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 分润fegin端的实现类
 */
@Component
public class ShareBenefitFeignImpl implements ShareBenefitFeign {

    /**
     * 机构后台分润查询
     * @param queryAgencyShareBenefitDTO
     * @return
     */
    @Override
    public BaseResponse getAgencyShareBenefit(QueryAgencyShareBenefitDTO queryAgencyShareBenefitDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 机构后台分润导出
     * @param exportAgencyShareBenefitDTO
     * @return
     */
    @Override
    public List<QueryAgencyShareBenefitVO> exportAgencyShareBenefit(ExportAgencyShareBenefitDTO exportAgencyShareBenefitDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
