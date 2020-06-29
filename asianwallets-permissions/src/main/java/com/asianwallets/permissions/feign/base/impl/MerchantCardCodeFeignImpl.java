package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.MerchantCardCodeDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.MerchantCardCodeFeign;
import org.springframework.stereotype.Component;

/**
 * 商户码牌的实现类
 */
@Component
public class MerchantCardCodeFeignImpl implements MerchantCardCodeFeign {
    /**
     * 分页查询商户码牌信息信息
     * @param merchantCardCodeDTO
     * @return
     */
    @Override
    public BaseResponse pageFindPreOrders(MerchantCardCodeDTO merchantCardCodeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 查询商户码牌详情信息
     * @param merchantCardCodeDTO
     * @return
     */
    @Override
    public BaseResponse getMerchantCardCode(MerchantCardCodeDTO merchantCardCodeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 修改商户码牌信息
     * @param merchantCardCodeDTO
     * @return
     */
    @Override
    public BaseResponse updateMerchantCardCode(MerchantCardCodeDTO merchantCardCodeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
