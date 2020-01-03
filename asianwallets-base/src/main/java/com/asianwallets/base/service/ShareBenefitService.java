package com.asianwallets.base.service;
import com.asianwallets.common.dto.ExportAgencyShareBenefitDTO;
import com.asianwallets.common.dto.QueryAgencyShareBenefitDTO;
import com.asianwallets.common.vo.QueryAgencyShareBenefitVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 分润服务
 */
public interface ShareBenefitService {

    /**
     * 分润分页查询
     * @param queryAgencyShareBenefitDTO
     * @return
     */
    PageInfo<QueryAgencyShareBenefitVO> getAgencyShareBenefit(QueryAgencyShareBenefitDTO queryAgencyShareBenefitDTO);

    /**
     * 分润导出查询用
     * @param exportAgencyShareBenefitDTO
     * @return
     */
    List<QueryAgencyShareBenefitVO> exportAgencyShareBenefit(ExportAgencyShareBenefitDTO exportAgencyShareBenefitDTO);
}
