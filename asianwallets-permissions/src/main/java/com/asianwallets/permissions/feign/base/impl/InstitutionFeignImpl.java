package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.InstitutionFeign;
import org.springframework.stereotype.Component;


/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-26 10:59
 **/
@Component
public class InstitutionFeignImpl implements InstitutionFeign {

    @Override
    public BaseResponse getInstitutionInfoById(String id) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse addInstitution(InstitutionDTO institutionDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateInstitution(InstitutionDTO institutionDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindInstitution(InstitutionDTO institutionDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindInstitutionAudit(InstitutionDTO institutionDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getInstitutionInfo(String id) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getInstitutionInfoAudit(String id) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse auditInstitution(String institutionId, Boolean enabled, String remark) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
