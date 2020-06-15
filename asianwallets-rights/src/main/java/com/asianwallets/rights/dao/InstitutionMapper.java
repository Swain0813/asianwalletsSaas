package com.asianwallets.rights.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Institution;
import org.springframework.stereotype.Repository;

/**
 * 机构Mapper
 */
@Repository
public interface InstitutionMapper extends BaseMapper<Institution> {

    /**
     * 根据机构code获取机构信息
     * @param institutionCode
     * @return
     */
    Institution selectByInstitutionCode(String institutionCode);
}