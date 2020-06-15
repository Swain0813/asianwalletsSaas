package com.asianwallets.permissions.feign.rights.impl;

import com.asianwallets.common.dto.RightsOrdersDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.RightsOrdersVO;
import com.asianwallets.permissions.feign.rights.RightsOrdersFeign;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RightsOrdersFeignImpl implements RightsOrdersFeign {

    @Override
    public BaseResponse pageRightsOrders(RightsOrdersDTO rightsOrdersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getRightsOrdersInfo(RightsOrdersDTO rightsOrdersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<RightsOrdersVO> exportRightsOrders(RightsOrdersDTO rightsOrdersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
