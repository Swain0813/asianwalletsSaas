package com.asianwallets.base.service;


import com.asianwallets.common.dto.MccChannelDTO;

/**
 * mcc 映射表
 */
public interface MccChannelService {
    /**
     * 添加
     *
     * @param mc
     * @return
     */
    int addMccChannel(MccChannelDTO mc);
}
