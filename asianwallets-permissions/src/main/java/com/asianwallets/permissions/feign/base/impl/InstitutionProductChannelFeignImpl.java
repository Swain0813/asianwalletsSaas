package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.InstitutionProductChannelDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.InstitutionProductChannelFeign;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InstitutionProductChannelFeignImpl implements InstitutionProductChannelFeign {

    @Override
    public BaseResponse addInsProCha(List<InstitutionProductChannelDTO> institutionProductChannelDTOList) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateInsProCha(List<InstitutionProductChannelDTO> institutionProductChannelDTOList) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getInsProChaByInsId(String insId) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getAllProCha() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
