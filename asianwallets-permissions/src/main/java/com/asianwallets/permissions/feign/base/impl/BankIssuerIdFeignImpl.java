package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.BankIssuerIdDTO;
import com.asianwallets.common.entity.BankIssuerId;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.BankIssuerIdVO;
import com.asianwallets.permissions.feign.base.BankIssuerIdFeign;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BankIssuerIdFeignImpl implements BankIssuerIdFeign {

    @Override
    public BaseResponse addBankIssuerId(List<BankIssuerIdDTO> bankIssuerIdDTOList) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateBankIssuerId(BankIssuerIdDTO bankIssuerIdDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindBankIssuerId(BankIssuerIdDTO bankIssuerIdDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<BankIssuerIdVO> exportBankIssuerId(BankIssuerIdDTO bankIssuerIdDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse importBankIssuerId(List<BankIssuerId> bankIssuerIdList) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
