package com.asianwallets.rights.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Attestation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttestationMapper extends BaseMapper<Attestation> {
    /**
     * 查询 institutionCode 机构code 对应的签名信息
     *
     * @param institutionCode
     * @return
     */
    Attestation selectByInstitutionCode(@Param("institutionCode") String institutionCode);

}