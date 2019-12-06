package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.InstitutionChannel;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface InstitutionChannelMapper extends BaseMapper<InstitutionChannel> {

    /**
     * 根据机构产品ID删除机构通道信息
     *
     * @param insProIdSet 机构产品ID集合
     * @return 修改条数
     */
    int deleteByInsProIdList(Set<String> insProIdSet);
}