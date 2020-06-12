package com.asianwallets.rights.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.InstitutionRightsDTO;
import com.asianwallets.common.dto.InstitutionRightsExportDTO;
import com.asianwallets.common.dto.InstitutionRightsInfoApiDTO;
import com.asianwallets.common.dto.InstitutionRightsQueryDTO;
import com.asianwallets.common.dto.InstitutionRightsPageDTO;
import com.asianwallets.common.entity.InstitutionRights;
import com.asianwallets.common.vo.InstitutionRightsApiVO;
import com.asianwallets.common.vo.InstitutionRightsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权益管理
 */
@Repository
public interface InstitutionRightsMapper extends BaseMapper<InstitutionRights> {

    /**
     * 权益分页查询
     *
     * @param institutionRightsDTO 查询DTO
     * @return List<InstitutionRightsVO>
     */
    List<InstitutionRightsVO> pageRightsInfo(InstitutionRightsPageDTO institutionRightsDTO);

    /**
     * 查询详情
     *
     * @param institutionRightsDTO 查询DTO
     * @return List<InstitutionRights>
     */
    InstitutionRights selectRightsInfo(InstitutionRightsDTO institutionRightsDTO);

    /**
     * 对外的查询
     *
     * @param institutionRightsInfoApiDTO 查询DTO
     * @return List<InstitutionRights>
     */
    List<InstitutionRightsApiVO> getRightsInfo(InstitutionRightsInfoApiDTO institutionRightsInfoApiDTO);

    /**
     * 查询当前请求机构权益信息还在活动周期的有效机构权益信息
     *
     * @return
     */
    List<InstitutionRights> getRightsInfoLists();

    /**
     * 根据机构权益订单号查询机构权益是否存在
     *
     * @param batchNo
     * @return
     */
    InstitutionRights getInstitutionRights(String batchNo);

    /**
     * 查找主体是否重复
     *
     * @param activityTheme
     * @return
     */
    InstitutionRights selectByActivityTheme(@Param("activityTheme") String activityTheme);

    /**
     * 机构权益导出用
     *
     * @param institutionRightsDTO
     * @return
     */
    List<InstitutionRightsVO> exportRightsInfo(InstitutionRightsExportDTO institutionRightsDTO);

    /**
     * 根据批次号更新剩余数量
     *
     * @param surplusAmount 剩余数量
     * @param batchNo       批次号
     * @return 修改条数
     */
    int updateSurplusAmountByBatchNo(@Param("surplusAmount") Integer surplusAmount, @Param("batchNo") String batchNo);

    /**
     *新增查询机构权益信息下拉框用
     * @param institutionRightsDTO
     * @return
     */
    List<InstitutionRights> pageRightsInfoList(InstitutionRightsQueryDTO institutionRightsDTO);
}