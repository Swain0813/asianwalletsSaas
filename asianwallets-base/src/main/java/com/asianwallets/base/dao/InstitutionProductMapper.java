package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.entity.InstitutionProduct;
import com.asianwallets.common.vo.InstitutionProductChannelVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstitutionProductMapper extends BaseMapper<InstitutionProduct> {

    /**
     * 根据机构ID查询机构条数信息
     *
     * @param institutionId 机构ID
     * @return 机构产品集合
     */
    int selectCountByInstitutionId(String institutionId);

    /**
     * 根据机构ID查询机构产品信息
     *
     * @param institutionId 机构ID
     * @return 机构产品集合
     */
    InstitutionProduct selectByInstitutionId(String institutionId);

    /**
     * 根据机构ID查询机构产品ID信息
     *
     * @param institutionId 机构ID
     * @return 机构产品ID集合
     */
    List<String> selectIdListByInstitutionId(String institutionId);

    /**
     * 根据机构ID查询机构关联产品通道信息
     *
     * @param merchantId 机构ID
     * @param language   语言
     * @return 机构产品通道信息集合
     */
    List<InstitutionProductChannelVO> selectRelevantInfoByInstitutionId(@Param("institutionId") String institutionId, @Param("merchantId") String merchantId, @Param("language") String language);


    /**
     * 根据机构ID删除机构通道信息
     *
     * @param institutionId 机构ID
     * @return 修改条数
     */
    int deleteByInstitutionId(String institutionId);

    /**
     * 分页查询机构参数设置
     *
     * @param institutionRequestDTO
     * @return
     */
    List<InstitutionProduct> pageInstitutionRequests(InstitutionRequestDTO institutionRequestDTO);
}