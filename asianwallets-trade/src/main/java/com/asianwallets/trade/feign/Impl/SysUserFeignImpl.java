package com.asianwallets.trade.feign.Impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.trade.feign.SysUserFeign;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-04-12 15:12
 **/
@Component
public class SysUserFeignImpl  implements SysUserFeign {

    @Override
    public BaseResponse checkPassword(String oldPassword, String password) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
