package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.ExportAgencyShareBenefitDTO;
import com.asianwallets.common.dto.QueryAgencyShareBenefitDTO;
import com.asianwallets.common.dto.ShareBenefitStatisticalDTO;
import com.asianwallets.common.entity.ShareBenefitLogs;
import com.asianwallets.common.vo.QueryAgencyShareBenefitVO;
import com.asianwallets.common.vo.ShareBenefitStatisticalVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 分润服务的数据层
 */
@Repository
public interface ShareBenefitLogsMapper extends BaseMapper<ShareBenefitLogs> {

    /**
     *分润分页查询
     * @param queryAgencyShareBenefitDTO
     * @return
     */
    List<QueryAgencyShareBenefitVO> pageAgencyShareBenefit(QueryAgencyShareBenefitDTO queryAgencyShareBenefitDTO);

    /**
     * 分润导出查询用
     * @param exportAgencyShareBenefitDTO
     * @return
     */
    List<QueryAgencyShareBenefitVO> exportAgencyShareBenefit(ExportAgencyShareBenefitDTO exportAgencyShareBenefitDTO);

    /**
     * @Author YangXu
     * @Date 2020/4/7
     * @Descripate 分润统计
     * @return
     **/
    List<ShareBenefitStatisticalVO> shareBenefitStatistical(ShareBenefitStatisticalDTO shareBenefitStatisticalDTO);
}
