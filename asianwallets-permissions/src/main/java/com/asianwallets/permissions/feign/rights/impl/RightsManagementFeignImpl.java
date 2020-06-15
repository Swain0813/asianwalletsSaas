package com.asianwallets.permissions.feign.rights.impl;
import com.asianwallets.common.dto.InstitutionRightsDTO;
import com.asianwallets.common.dto.InstitutionRightsExportDTO;
import com.asianwallets.common.dto.InstitutionRightsPageDTO;
import com.asianwallets.common.dto.InstitutionRightsQueryDTO;
import com.asianwallets.common.entity.InstitutionRights;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.InstitutionRightsVO;
import com.asianwallets.permissions.feign.rights.RightsManagementFeign;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RightsManagementFeignImpl implements RightsManagementFeign {

    @Override
    public BaseResponse addRights(InstitutionRightsDTO institutionRightsDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageRightsInfo(InstitutionRightsPageDTO institutionRightsDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse selectRightsInfo(InstitutionRightsDTO institutionRightsDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateRightsInfo(InstitutionRightsDTO institutionRightsDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<InstitutionRightsVO> exportRightsInfo(InstitutionRightsExportDTO institutionRightsDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse importRightsInfo(List<InstitutionRights> institutionRights) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 权益发放管理用查询机构权益信息
     * @return
     */
    @Override
    public List<InstitutionRights> getRightsInfoLists() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 新增查询机构权益信息下拉框用
     * @param institutionRightsQueryDTO
     * @return
     */
    @Override
    public BaseResponse pageRightsInfoList(InstitutionRightsQueryDTO institutionRightsQueryDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
