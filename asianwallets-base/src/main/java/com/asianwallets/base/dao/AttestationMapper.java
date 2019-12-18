package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.AttestationDTO;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.vo.AttestationVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttestationMapper extends BaseMapper<Attestation> {

    /**
     * @param attestationDTO
     * @return
     */
    List<AttestationVO> selectKeyInfo(AttestationDTO attestationDTO);


    /**
     * 查询公钥
     *
     * @param institutionCode
     * @return
     */
    AttestationVO selectPlatformPub(String institutionCode);

    /**
     * 通过merchantId查询密钥
     *
     * @param merchantId
     * @return
     */
    int selectByMerchantId(@Param("merchantId") String merchantId);

    /**
     * 通过公钥查询个数
     *
     * @param pubKey
     * @return
     */
    int selectByPubKey(@Param("pubKey") String pubKey);

}