package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.AttestationDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.AttestationFeign;
import org.springframework.stereotype.Component;

/**
 * @author shenxinran
 * @Date: 2019/2/18 18:24
 * @Description: 密钥管理Feign熔断器
 */
@Component
public class AttestationFeignImpl implements AttestationFeign {
    /**
     * 生成RSA公私钥
     *
     * @return
     */
    @Override
    public BaseResponse getRSA() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 查询商户密钥列表
     *
     * @param attestationDTO
     * @return
     */
    @Override
    public BaseResponse pageKeyInfo(AttestationDTO attestationDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 更新密钥
     *
     * @param attestationDTO
     * @return
     */
    @Override
    public BaseResponse updateKeyInfo(AttestationDTO attestationDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
