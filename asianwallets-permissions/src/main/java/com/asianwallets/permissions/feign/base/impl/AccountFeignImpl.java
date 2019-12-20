package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.AccountListVO;
import com.asianwallets.permissions.feign.base.AccountFeign;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 账户信息feign端的实现类
 */
@Component
public class AccountFeignImpl implements AccountFeign {


    /**
     * 分页查询账户信息
     * @param accountSearchDTO
     * @return
     */
    @Override
    public BaseResponse pageFindAccount(AccountSearchDTO accountSearchDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 导出账户信息
     * @param accountSearchDTO
     * @return
     */
    @Override
    public List<AccountListVO> exportAccountList(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
