package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.ChannelBank;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Repository;


@Repository
public interface ChannelBankMapper extends BaseMapper<ChannelBank> {

    /**
     * 根据通道id删除通道银行中间表的记录
     * @param channelId
     */
    @Delete("DELETE FROM channel_bank WHERE channel_id = #{channelId}")
    int deleteByChannelId(String channelId);
}