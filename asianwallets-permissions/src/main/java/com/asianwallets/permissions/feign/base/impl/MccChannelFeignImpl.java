package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.MccChannelDTO;
import com.asianwallets.common.entity.MccChannel;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.MccChannelVO;
import com.asianwallets.permissions.feign.base.MccChannelFeign;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * mcc
 */
@Component
public class MccChannelFeignImpl implements MccChannelFeign {
    @Override
    public BaseResponse addMccChannel(MccChannelDTO mc) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageMccChannel(MccChannelDTO mc) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse banMccChannel(MccChannelDTO mc) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse inquireAllMccChannel() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse importMccChannel(List<MccChannel> list) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<MccChannelVO> exportMccChannel(MccChannelDTO mc) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
