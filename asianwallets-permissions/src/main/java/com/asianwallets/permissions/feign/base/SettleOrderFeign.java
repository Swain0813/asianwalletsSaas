package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.SettleOrderDTO;
import com.asianwallets.common.entity.SettleOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.SettleOrderFeignImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 机构结算交易
 */
@FeignClient(value = "asianwallets-base", fallback = SettleOrderFeignImpl.class)
public interface SettleOrderFeign {

    /**
     * 机构结算交易分页一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    @PostMapping("/finance/pageSettleOrder")
    BaseResponse pageSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO);


    /**
     * 机构结算交易分页详情
     *
     * @param settleOrderDTO
     * @return
     */
    @PostMapping("/finance/pageSettleOrderDetail")
    BaseResponse pageSettleOrderDetail(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO);

    /**
     * 导出详情
     *
     * @param settleOrderDTO
     * @return
     */
    @PostMapping("/finance/exportSettleOrder")
    List<SettleOrder> exportSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO);

}
