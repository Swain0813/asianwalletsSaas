package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.SearchAccountCheckDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.AccountCheckFeign;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AccountCheckFeignImpl implements AccountCheckFeign {

    @Override
    public BaseResponse pageAccountCheckLog(SearchAccountCheckDTO searchAccountCheckDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse channelAccountCheck(MultipartFile file) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageAccountCheck(SearchAccountCheckDTO searchAccountCheckDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse exportAccountCheck(SearchAccountCheckDTO searchAccountCheckDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateCheckAccount(String checkAccountId, String remark) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageAccountCheckAudit(SearchAccountCheckDTO searchAccountCheckDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse exportAccountCheckAudit(SearchAccountCheckDTO searchAccountCheckDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse auditCheckAccount(String checkAccountId, Boolean enable, String remark) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
