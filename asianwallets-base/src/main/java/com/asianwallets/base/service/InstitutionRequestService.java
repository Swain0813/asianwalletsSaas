package com.asianwallets.base.service;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.entity.InstitutionRequestParameters;
import com.github.pagehelper.PageInfo;
import java.util.List;

/**
 * 机构请求参数设置相关接口
 */
public interface InstitutionRequestService {

    /**
     * 新增机构请求参数设置
     * @param username
     * @param institutionRequests
     * @return
     */
    int addInstitutionRequest(String username, List<InstitutionRequestDTO> institutionRequests);


    /**
     * 分页查询机构请求参数设置
     * @param institutionRequestDTO
     * @return
     */
    PageInfo<InstitutionRequestParameters> pageInstitutionRequest(InstitutionRequestDTO institutionRequestDTO);

    /**
     *根据机构编号查询机构请求参数设置
     * @param institutionRequestDTO
     * @return
     */
    List<InstitutionRequestParameters> getInstitutionRequests(InstitutionRequestDTO institutionRequestDTO);

    /**
     * 修改机构请求参数设置
     * @param username
     * @param institutionRequests
     * @return
     */
    int updateInstitutionRequest(String username, List<InstitutionRequestDTO> institutionRequests);
}
