package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.MerchantProduct;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.MerChannelVO;
import com.asianwallets.permissions.feign.base.MerchantProductFeign;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-10 16:57
 **/
@Component
public class MerchantProductFeignImpl implements MerchantProductFeign {

    @Override
    public BaseResponse addMerchantProduct(List<MerchantProductDTO> merchantProductDTOs) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateMerchantProduct(MerchantProductDTO merchantProductDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse auditMerchantProduct(AuaditProductDTO auaditProductDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse allotMerProductChannel(@Valid MerProDTO merProDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindMerProduct(MerchantProductDTO merchantProductDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getMerProductById(String merProductId) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindMerProductAudit(MerchantProductDTO merchantProductDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getMerProductAuditById(String merProductId) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindMerProChannel(SearchChannelDTO searchChannelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getMerChannelInfoById(String merChannelId) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateMerchantChannel(List<BatchUpdateSortDTO> batchUpdateSort) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getRelevantInfo(String merchantId) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<MerchantProduct> exportMerProduct(MerchantProductDTO merchantProductDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<MerChannelVO> exportMerChannel(SearchChannelDTO searchChannelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
