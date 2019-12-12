package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.ExportBankVO;
import com.asianwallets.permissions.feign.base.BankFeign;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BankFeignImpl implements BankFeign {

    @Override
    public BaseResponse addBank(BankDTO bankDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateBank(BankDTO bankDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindBank(BankDTO bankDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<ExportBankVO> exportBank(BankDTO bankDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse importBank(List<Bank> bankList) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public Bank getByBankNameAndCurrency(BankDTO bankDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<String> getAllBankName() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
