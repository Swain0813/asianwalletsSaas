package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.PreOrdersDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.ExportPreOrdersVO;
import com.asianwallets.permissions.feign.base.PreOrdersFeign;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 预授权订单的feign的实现类
 */
@Component
public class PreOrdersFeignImpl implements PreOrdersFeign {

    @Override
    public BaseResponse pageFindPreOrders(PreOrdersDTO preOrdersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getPreOrdersDetail(PreOrdersDTO preOrdersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<ExportPreOrdersVO> exportPreOrders(PreOrdersDTO preOrdersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
