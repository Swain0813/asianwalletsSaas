package com.asianwallets.permissions.feign.trade.impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.trade.RefundTradeFeign;
import org.springframework.stereotype.Component;

/**
 * 退款feign相关模块的实现类
 */
@Component
public class RefundTradeFeignImpl implements RefundTradeFeign {
    /**
     * 人工退款接口
     * @param refundOrderId
     * @param enabled
     * @param remark
     * @return
     */
    @Override
    public BaseResponse artificialRefund(String refundOrderId, Boolean enabled, String remark) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

}
