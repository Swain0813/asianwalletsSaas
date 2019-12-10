package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.vo.MChannelVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelMapper extends BaseMapper<Channel> {

    /**
     * 使用CODE 查询 channel 信息
     *
     * @param code
     * @return
     */
    Channel selectByChannelCode(@Param("code") String code);

    /**
     * @return MChannelVO 这里面的CID指的是channel_code
     * @date 2019
     * @deprecated 查询所有通道
     */
    List<MChannelVO> selectAllChannel();
}