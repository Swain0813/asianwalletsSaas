package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.ProductChannel;
import com.asianwallets.common.vo.ProductChannelVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductChannelMapper extends BaseMapper<ProductChannel> {

    /**
     * 根据通道id删除产品通道中间表的记录
     *
     * @param channelId 通道ID
     */
    @Delete("DELETE FROM product_channel WHERE channel_id = #{channelId}")
    int deleteByChannelId(@Param("channelId") String channelId);

    /**
     * 查询所有产品关联通道信息
     *
     * @return 产品通道集合
     */
    List<ProductChannelVO> getAllProCha(String language);

}