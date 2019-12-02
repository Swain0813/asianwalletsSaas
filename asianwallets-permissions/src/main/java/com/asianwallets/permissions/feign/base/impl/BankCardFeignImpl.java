package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.BankCardDTO;
import com.asianwallets.common.dto.BankCardSearchDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.BankCardFeign;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 结算信息管理
 * @author: YangXu
 * @create: 2019-12-02 10:56
 **/
@Component
public class BankCardFeignImpl implements BankCardFeign {

    @Override
    public BaseResponse addBankCard(List<BankCardDTO> bankCardDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateBankCard(BankCardDTO bankCardDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse selectBankCardByMerId(String merchantId) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageBankCard(BankCardSearchDTO bankCardSearchDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse banBankCard(String bankCardId, Boolean enabled) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse defaultBankCard(String bankCardId, Boolean defaultFlag) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
