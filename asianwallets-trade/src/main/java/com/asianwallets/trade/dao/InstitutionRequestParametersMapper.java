package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;

import com.asianwallets.common.entity.InstitutionRequestParameters;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRequestParametersMapper extends BaseMapper<InstitutionRequestParameters> {

    /**
     * 根据机构ID和交易方向查询机构请求参数
     *
     * @param institutionId  机构ID
     * @param tradeDirection 交易方向
     * @return 机构请求参数
     */
    InstitutionRequestParameters selectByInstitutionIdAndTradeDirection(@Param("institutionId") String institutionId, @Param("tradeDirection") Byte tradeDirection);
}