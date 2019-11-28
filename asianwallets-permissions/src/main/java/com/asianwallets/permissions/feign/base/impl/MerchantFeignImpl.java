package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.MerchantFeign;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-26 11:01
 **/
@Component
public class MerchantFeignImpl implements MerchantFeign {

    @Override
    public BaseResponse addMerchant(MerchantDTO merchantDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateMerchant(MerchantDTO merchantDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindMerchant(MerchantDTO merchantDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindMerchantAudit(MerchantDTO merchantDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getMerchantInfo(String id) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getMerchantAuditInfo(String id) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse auditMerchant(String merchantId, Boolean enabled, String remark) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getAllAgent(String merchantType) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse exportMerchant(MerchantDTO merchantDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse banMerchant(String merchantId, Boolean enabled) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }


}
