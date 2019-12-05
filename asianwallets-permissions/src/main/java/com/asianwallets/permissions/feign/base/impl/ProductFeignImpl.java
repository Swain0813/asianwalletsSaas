package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.ProductDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.ProductFeign;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-05 14:01
 **/
@Component
public class ProductFeignImpl implements ProductFeign {

    @Override
    public BaseResponse addProduct(ProductDTO productDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateProduct(ProductDTO productDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageProduct(ProductDTO productDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
