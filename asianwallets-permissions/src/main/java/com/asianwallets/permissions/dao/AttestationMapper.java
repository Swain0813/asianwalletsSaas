package com.asianwallets.permissions.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Attestation;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface AttestationMapper extends BaseMapper<Attestation> {

    /**
     * 查询平台的公钥
     *
     * @return
     */
    @Select("select * from attestation where merchant_id=#{key} and enabled = true")
    Attestation selectPlatformPub(String key);


}