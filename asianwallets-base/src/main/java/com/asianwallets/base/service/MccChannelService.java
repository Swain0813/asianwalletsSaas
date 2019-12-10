package com.asianwallets.base.service;


import com.asianwallets.common.dto.MccChannelDTO;
import com.asianwallets.common.entity.MccChannel;
import com.asianwallets.common.vo.MccChannelExportVO;
import com.asianwallets.common.vo.MccChannelVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * mcc 映射表
 */
public interface MccChannelService {
    /**
     * 添加
     *
     * @param mc
     * @return
     */
    int addMccChannel(MccChannelDTO mc);

    /**
     * 分页查询
     *
     * @param mc
     * @return
     */
    PageInfo<MccChannelVO> pageMccChannel(MccChannelDTO mc);

    /**
     * 禁用启用 映射表
     *
     * @param mc
     * @return
     */
    int banMccChannel(MccChannelDTO mc);

    /**
     * 查询所有数据
     *
     * @param language
     * @return
     */
    List<MccChannelVO> inquireAllMccChannel(String language);

    /**
     * 导入
     *
     * @param list
     * @return
     */
    int importMccChannel(List<MccChannel> list);

    /**
     * 导出
     *
     * @param mc
     * @return
     */
    List<MccChannelExportVO> exportMccChannel(MccChannelDTO mc);
}
