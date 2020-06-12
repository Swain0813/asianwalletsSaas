package com.asianwallets.rights.service;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.InstitutionRights;
import com.asianwallets.common.vo.InstitutionRightsApiVO;
import com.asianwallets.common.vo.InstitutionRightsInfoVO;
import com.asianwallets.common.vo.InstitutionRightsVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

/**
 * 机构权益管理
 */
public interface RightsManagementService {

    /**
     * 新增权益
     *
     * @param institutionRightsDTO 查询DTO
     * @param creator
     * @return int
     */
    int addRights(InstitutionRightsDTO institutionRightsDTO, String creator);

    /**
     * 分页查询
     *
     * @param institutionRightsDTO 查询DTO
     * @return PageInfo<InstitutionRightsVO>
     */
    PageInfo<InstitutionRightsVO> pageRightsInfo(InstitutionRightsPageDTO institutionRightsDTO);

    /**
     * 查询详情
     *
     * @param institutionRightsDTO 查询DTO
     * @return List<InstitutionRights>
     */
    InstitutionRightsInfoVO selectRightsInfo(InstitutionRightsDTO institutionRightsDTO);

    /**
     * 导出
     *
     * @param institutionRightsExportDTO 查询DTO
     * @return List<InstitutionRightsVO>
     */
    List<InstitutionRightsVO> exportRightsInfo(InstitutionRightsExportDTO institutionRightsExportDTO);

    /**
     * 修改权益
     *
     * @param institutionRightsDTO 修改DTO
     * @param updateName
     * @return INT
     */
    int updateRightsInfo(InstitutionRightsDTO institutionRightsDTO, String updateName);

    /**
     * 导入数据
     *
     * @param institutionRights 导入实体
     * @return INT
     */
    int importRightsInfo(List<InstitutionRights> institutionRights);

    /**
     * 对外的查询方法
     *
     * @param institutionRightsInfoApiDTO 查询DTO
     * @return List<InstitutionRights>
     */
    List<InstitutionRightsApiVO> getRightsInfo(InstitutionRightsInfoApiDTO institutionRightsInfoApiDTO);

    /**
     * 权益发放管理用查询机构权益信息
     *
     * @return List<InstitutionRights>
     */
    List<InstitutionRights> getRightsInfoLists();

    /**
     * 对外的权益新增
     *
     * @param institutionRightsApiDTO 对外的权益新增DTO
     * @return int
     */
    int addRightsApi(RightsApiDTO institutionRightsApiDTO);

    /**
     * 新增查询机构权益信息下拉框用
     * @param institutionRightsQueryDTO
     * @return
     */
    PageInfo<InstitutionRights> pageRightsInfoList(InstitutionRightsQueryDTO institutionRightsQueryDTO);
}
