package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.InstitutionProduct;
import com.asianwallets.common.vo.InstitutionProductChannelVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstitutionProductMapper extends BaseMapper<InstitutionProduct> {

    /**
     * 根据机构ID查询机构产品信息
     *
     * @param institutionId 机构ID
     * @return 修改条数
     */
    InstitutionProduct selectByInstitutionId(String institutionId);

    /**
     * 根据机构ID查询机构关联产品通道信息
     *
     * @param institutionId 机构ID
     * @return 修改条数
     */
    List<InstitutionProductChannelVO> selectRelevantInfoByInstitutionId(String institutionId);


    /**
     * 根据机构ID删除机构通道信息
     *
     * @param institutionId 机构ID
     * @return 修改条数
     */
    int deleteByInstitutionId(String institutionId);
}