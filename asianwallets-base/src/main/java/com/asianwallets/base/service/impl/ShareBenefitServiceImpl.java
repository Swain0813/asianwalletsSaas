package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.ShareBenefitLogsMapper;
import com.asianwallets.base.service.ShareBenefitService;
import com.asianwallets.common.dto.ExportAgencyShareBenefitDTO;
import com.asianwallets.common.dto.QueryAgencyShareBenefitDTO;
import com.asianwallets.common.dto.ShareBenefitStatisticalDTO;
import com.asianwallets.common.vo.QueryAgencyShareBenefitVO;
import com.asianwallets.common.vo.ShareBenefitStatisticalVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分润服务的实现类
 */
@Service
public class ShareBenefitServiceImpl implements ShareBenefitService {

    @Autowired
    private ShareBenefitLogsMapper shareBenefitLogsMapper;

    /**
     *分润分页查询
     * @param queryAgencyShareBenefitDTO
     * @return
     */
    @Override
    public PageInfo<QueryAgencyShareBenefitVO> getAgencyShareBenefit(QueryAgencyShareBenefitDTO queryAgencyShareBenefitDTO) {
        return new PageInfo<>(shareBenefitLogsMapper.pageAgencyShareBenefit(queryAgencyShareBenefitDTO));
    }

    /**
     * 分润导出查询用
     * @param exportAgencyShareBenefitDTO
     * @return
     */
    @Override
    public List<QueryAgencyShareBenefitVO> exportAgencyShareBenefit(ExportAgencyShareBenefitDTO exportAgencyShareBenefitDTO) {
        return shareBenefitLogsMapper.exportAgencyShareBenefit(exportAgencyShareBenefitDTO);
    }

    /**
     * @Author YangXu
     * @Date 2020/4/7
     * @Descripate 分润统计
     * @return
     **/
    @Override
    public List<ShareBenefitStatisticalVO> shareBenefitStatistical(ShareBenefitStatisticalDTO shareBenefitStatisticalDTO) {
        return shareBenefitLogsMapper.shareBenefitStatistical(shareBenefitStatisticalDTO);
    }
}
