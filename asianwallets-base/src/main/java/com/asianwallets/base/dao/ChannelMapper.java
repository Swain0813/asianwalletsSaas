package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.AgentChannelsDTO;
import com.asianwallets.common.dto.ChannelDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.vo.AgentChannelsVO;
import com.asianwallets.common.vo.ChannelDetailVO;
import com.asianwallets.common.vo.ChannelExportVO;
import com.asianwallets.common.vo.ChannelVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelMapper extends BaseMapper<Channel> {

    /**
     * 使用通道编码查询通道信息
     *
     * @param channelCode 通道编码
     * @return 通道
     */
    Channel selectByChannelCode(String channelCode);

    /**
     * 使用通道中文名称与通道币种查询通道信息
     *
     * @param channelCnName 通道中文名称
     * @param currency      通道币种
     * @return 通道
     */
    Channel selectByNameAndCurrency(@Param("channelCnName") String channelCnName, @Param("currency") String currency);

    /**
     * 分页查询通道信息
     *
     * @param channelDTO 通道输入实体
     * @return 修改条数
     */
    List<ChannelVO> pageFindChannel(ChannelDTO channelDTO);

    /**
     * 根据通道ID查询通道详情
     *
     * @param channelId 通道ID
     * @param language  语言
     * @return 通道详情输出实体
     */
    ChannelDetailVO selectByChannelId(@Param("channelId") String channelId, @Param("language") String language);

    /**
     * 导出通道信息
     *
     * @param channelDTO 通道输入实体
     * @return ChannelExportVO集合
     */
    List<ChannelExportVO> exportChannel(ChannelDTO channelDTO);

    /**
     * 查询所有通道编号
     *
     * @return 通道编号集合
     */
    List<String> selectAllChannelCode();

    /**
     * 代理商渠道查询
     *
     * @param agentChannelsDTO
     * @return
     */
    List<AgentChannelsVO> pageAgentChannels(AgentChannelsDTO agentChannelsDTO);

    /**
     * 根据通道服务名称获取所有的通道编号
     * @param name
     * @return
     */
    @Select("select channel_code from channel where channel_en_name like CONCAT(CONCAT('%', #{name}), '%') ")
    List<String> getChannelCodeByName(@Param("name") String name);
}