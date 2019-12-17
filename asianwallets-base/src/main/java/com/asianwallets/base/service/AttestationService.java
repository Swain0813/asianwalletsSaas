package com.asianwallets.base.service;


import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.AttestationDTO;
import com.asianwallets.common.vo.AttestationVO;
import com.github.pagehelper.PageInfo;

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
     * 添加密钥
     *
     * @param attestationDTO
     * @return
     */
    int addKey(AttestationDTO attestationDTO);

    /**
     * 分页查询密钥
     *
     * @param attestationDTO
     * @return
     */
    List<AttestationVO> selectKeyInfo(AttestationDTO attestationDTO);

    /**
     * 查询密钥的所有信息 包含平台的私钥
     *
     * @param attestationDTO
     * @return
     */
    PageInfo<Attestation> pageAllKeyInfo(AttestationDTO attestationDTO);

    /**
     * 更新密钥信息
     *
     * @param attestationDTO
     * @return
     */
    int updateKeyInfo(AttestationDTO attestationDTO);

    /**
     * 启用禁用密钥
     *
     * @param attestationDTO
     * @return
     */
    int banKeyInfo(AttestationDTO attestationDTO);


}
