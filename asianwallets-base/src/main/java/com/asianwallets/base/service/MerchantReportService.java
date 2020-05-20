package com.asianwallets.base.service;
import com.asianwallets.common.dto.MerchantReportDTO;
import com.asianwallets.common.entity.MerchantReport;
import com.asianwallets.common.vo.MerchantReportVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

/**
 * 商户报备
 */
public interface MerchantReportService {

    /**
     * 添加报备
     *
     * @param merchantReportDTO
     * @return
     */
    int addReport(MerchantReportDTO merchantReportDTO);

    /**
     * 查询
     *
     * @param merchantReportDTO
     * @return
     */
    PageInfo<MerchantReportVO> pageReport(MerchantReportDTO merchantReportDTO);

    /**
     * 修改报备信息
     *
     * @param merchantReportDTO
     * @return
     */
    int updateReport(MerchantReportDTO merchantReportDTO);

    /**
     * 启用禁用报备信息
     *
     * @param merchantReportDTO
     * @return
     */
    int banReport(MerchantReportDTO merchantReportDTO);

    /**
     * 导出商户报备信息查询用
     *
     * @param merchantReportDTO
     * @return
     */
    List<MerchantReportVO> exportReport(MerchantReportDTO merchantReportDTO);

    /**
     * 导入商户报备信息
     * @param merchantReportList
     * @return
     */
    int importMerchantReport(List<MerchantReport> merchantReportList);
}
