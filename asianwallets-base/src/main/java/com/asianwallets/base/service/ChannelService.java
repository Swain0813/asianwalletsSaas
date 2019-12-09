package com.asianwallets.base.service;

import com.asianwallets.common.dto.ChannelDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.vo.ChannelDetailVO;
import com.github.pagehelper.PageInfo;

public interface ChannelService {


    /**
     * 添加通道信息
     *
     * @param username   用户名
     * @param channelDTO 通道输入实体
     * @return 修改条数
     */
    int addChannel(String username, ChannelDTO channelDTO);

    /**
     * 修改通道信息
     *
     * @param username   用户名
     * @param channelDTO 通道输入实体
     * @return 修改条数
     */
    int updateChannel(String username, ChannelDTO channelDTO);

    /**
     * 分页查询通道信息
     *
     * @param channelDTO 通道输入实体
     * @return 修改条数
     */
    PageInfo<Channel> pageFindChannel(ChannelDTO channelDTO);

    /**
     * 根据通道ID查询通道详情
     *
     * @param channelId 通道ID
     * @return 修改条数
     */
    ChannelDetailVO getChannelById(String channelId);
}
