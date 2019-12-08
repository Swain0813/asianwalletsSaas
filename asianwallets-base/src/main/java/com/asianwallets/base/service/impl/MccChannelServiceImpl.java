package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.MccChannelMapper;
import com.asianwallets.base.service.MccChannelService;
import com.asianwallets.common.dto.MccChannelDTO;
import com.asianwallets.common.entity.MccChannel;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MccChannelServiceImpl implements MccChannelService {

    @Autowired
    MccChannelMapper mccChannelMapper;

    /**
     * 添加
     *
     * @param mc
     * @return
     */
    @Override
    public int addMccChannel(MccChannelDTO mc) {
        if (StringUtils.isBlank(mc.getCid()) || StringUtils.isBlank(mc.getMid()) || StringUtils.isBlank(mc.getCode())) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (mccChannelMapper.selectByCidAndMid(mc) != null) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        MccChannel mccChannel = new MccChannel();
        BeanUtils.copyProperties(mc, mccChannel);
        mccChannel.setId("");
        return 0;
    }
}
