package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.ChannelDTO;
import com.asianwallets.common.entity.Channel;
import org.apache.ibatis.annotations.Param;
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
    List<Channel> pageFindChannel(ChannelDTO channelDTO);
}