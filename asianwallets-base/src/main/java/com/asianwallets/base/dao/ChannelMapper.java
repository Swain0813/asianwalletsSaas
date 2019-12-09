package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Channel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelMapper extends BaseMapper<Channel> {

    /**
     * 使用CODE 查询 channel 信息
     *
     * @param code
     * @return
     */
    Channel selectByChannelCode(@Param("code") String code);
}