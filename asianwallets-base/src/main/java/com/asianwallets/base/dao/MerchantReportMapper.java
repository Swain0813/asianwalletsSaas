package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.MerchantReportDTO;
import com.asianwallets.common.entity.MerchantReport;
import com.asianwallets.common.vo.MerchantReportVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantReportMapper extends BaseMapper<MerchantReport> {

    /**
     * 通过店铺编号查询报备信息
     *
     * @param shopCode
     * @return
     */
    MerchantReport selectByShopCode(@Param("shopCode") String shopCode);

    /**
     * 分页
     *
     * @param merchantReportDTO
     * @return
     */
    List<MerchantReportVO> pageReport(MerchantReportDTO merchantReportDTO);

    /**
     * Modify the status of Report
     *
     * @param id
     * @param enabled
     * @param modifier
     * @return
     */
    int banReport(@Param("id") String id, @Param("enabled") Boolean enabled, @Param("modifier") String modifier);

}