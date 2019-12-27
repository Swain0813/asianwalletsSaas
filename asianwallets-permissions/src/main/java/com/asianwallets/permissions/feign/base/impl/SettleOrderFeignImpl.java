package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.ReviewSettleDTO;
import com.asianwallets.common.dto.SettleOrderDTO;
import com.asianwallets.common.entity.SettleOrder;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.SettleOrderFeign;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 机构结算交易相关模块
 */
@Component
public class SettleOrderFeignImpl implements SettleOrderFeign {


    /**
     * 机构结算交易分页一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public BaseResponse pageSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 机构结算交易详情分页查询
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public BaseResponse pageSettleOrderDetail(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 导出详情
     *
     * @param settleOrderDTO
     * @return
     */
    @Override
    public List<SettleOrder> exportSettleOrder(SettleOrderDTO settleOrderDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 结算审核
     * @param reviewSettleDTO
     * @return
     */
    @Override
    public BaseResponse reviewSettlement(@RequestBody @ApiParam ReviewSettleDTO reviewSettleDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
