package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.ChannelDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.ChannelExportVO;
import com.asianwallets.permissions.feign.base.ChannelFeign;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChannelFeignImpl implements ChannelFeign {

    @Override
    public BaseResponse addChannel(ChannelDTO channelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateChannel(ChannelDTO channelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindChannel(ChannelDTO channelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getChannelById(String channelId) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<ChannelExportVO> exportChannel(ChannelDTO channelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<String> getAllChannelCode() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
