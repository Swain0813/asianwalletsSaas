package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.entity.InstitutionRequestParameters;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 机构请求参数设置数据层
 */
@Repository
public interface InstitutionRequestParametersMapper extends BaseMapper<InstitutionRequestParameters> {

    /**
     * 分页查询机构请求参数设置
     * @param institutionRequestDTO
     * @return
     */
    List<InstitutionRequestParameters> pageInstitutionRequest(InstitutionRequestDTO institutionRequestDTO);

    /**
     * 查询机构请求参数设置详情用
     * @param institutionRequestDTO
     * @return
     */
    List<InstitutionRequestParameters> getInstitutionRequests(InstitutionRequestDTO institutionRequestDTO);

    /**
     * 根据机构编号获取机构请求参数设置信息
     * @param institutionCode
     * @return
     */
    List<InstitutionRequestParameters> getInstitutionRequest(String institutionCode);
}