package com.asianwallets.base.service;

import com.asianwallets.common.dto.ChannelDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.vo.ChannelDetailVO;
import com.asianwallets.common.vo.ChannelExportVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

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
     * @return 通道详情输出实体
     */
    ChannelDetailVO getChannelById(String channelId);

    /**
     * 导出通道信息
     *
     * @param channelDTO 通道输入实体
     * @return List<ChannelExportVO>
     */
    List<ChannelExportVO> exportChannel(ChannelDTO channelDTO);

    /**
     * 查询所有通道编号
     *
     * @return 通道编号集合
     */
    List<String> getAllChannelCode();
}
