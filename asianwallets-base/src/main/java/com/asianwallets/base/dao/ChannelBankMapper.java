package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.ChannelBank;
import com.asianwallets.common.vo.ChaBankRelVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChannelBankMapper extends BaseMapper<ChannelBank> {

    /**
     * 根据通道id删除通道银行中间表的记录
     *
     * @param channelId
     */
    @Delete("DELETE FROM channel_bank WHERE channel_id = #{channelId}")
    int deleteByChannelId(String channelId);

    ChaBankRelVO getInfoByCIdAndBId(@Param("channelId") String channelId, @Param("bankId") String bankId);

    /**
     * 根据通道ID查询通道银行
     *
     * @param channelId 通道ID
     * @return 通道银行集合
     */
    List<ChannelBank> selectByChannelId(String channelId);
}
