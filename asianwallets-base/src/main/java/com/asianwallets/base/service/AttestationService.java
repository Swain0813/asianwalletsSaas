package com.asianwallets.base.service;


import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.AttestationDTO;
import com.asianwallets.common.entity.Attestation;
import com.asianwallets.common.vo.AttestationVO;

import java.util.List;
import java.util.Map;

/**
 * 密钥业务层
 */
public interface AttestationService extends BaseService<Attestation> {
    /**
     * 生成RSA公私钥
     *
     * @return
     */
    Map getRSA();

    /**
     * 分页查询密钥
     *
     * @param attestationDTO
     * @return
     */
    List<AttestationVO> selectKeyInfo(AttestationDTO attestationDTO);

    /**
     * 更新密钥信息
     *
     * @param attestationDTO
     * @return
     */
    int updateKeyInfo(AttestationDTO attestationDTO);
}
