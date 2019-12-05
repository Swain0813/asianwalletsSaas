package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.InstitutionChannel;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionChannelMapper extends BaseMapper<InstitutionChannel> {

    /**
     * 根据机构ID删除机构通道信息
     *
     * @param institutionId 机构ID
     * @return 修改条数
     */
    int deleteByInstitutionId(String institutionId);
}