package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.MccChannelDTO;
import com.asianwallets.common.entity.MccChannel;
import com.asianwallets.common.vo.MccChannelVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MccChannelMapper extends BaseMapper<MccChannel> {

    /**
     * 通过cid and mid 查询 映射表数据
     *
     * @param mc
     * @return
     */
    MccChannel selectByCidAndMid(MccChannelDTO mc);

    /**
     * 分页查询
     *
     * @param mc
     * @return
     */
    List<MccChannelVO> pageMccChannel(MccChannelDTO mc);

    /**
     * 查询所有数据
     *
     * @param language
     * @return
     */
    List<MccChannelVO> inquireAllMccChannel(@Param("language") String language);

    /**
     * 通过CODE查询
     *
     * @param code
     * @return
     */
    MccChannel selectByCode(@Param("code") String code);
}