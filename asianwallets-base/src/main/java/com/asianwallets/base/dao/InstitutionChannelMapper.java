package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.InstitutionChannel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstitutionChannelMapper extends BaseMapper<InstitutionChannel> {

    /**
     * 根据机构产品ID删除机构通道信息
     *
     * @param insProIdList 机构产品ID集合
     * @return 修改条数
     */
    int deleteByInsProIdList(List<String> insProIdList);
}