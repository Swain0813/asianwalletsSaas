package com.asianwallets.permissions.feign.rights.impl;

import com.asianwallets.common.dto.OtaChannelDTO;
import com.asianwallets.common.entity.OtaChannel;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.rights.OtaChannelFeign;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OtaChannelFeignImpl implements OtaChannelFeign {
    @Override
    public BaseResponse pageOtaChannel(OtaChannelDTO otaChannelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse addOtaChannel(OtaChannelDTO otaChannelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<OtaChannel> getOtaChannels() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
