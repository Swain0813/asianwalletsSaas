package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.SettleOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.SettleOrderFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 结算交易
 */
@FeignClient(value = "asianwallets-base", fallback = SettleOrderFeignImpl.class)
public interface SettleOrderFeign {

    /**
     * 结算交易分页一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    @PostMapping("/settleorders/pageSettleOrder")
    BaseResponse pageSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO);

    /**
     * 分页查询集团结算交易一览查询
     *
     * @param settleOrderDTO
     * @return
     */
    @PostMapping("/settleorders/pageGroupSettleOrder")
    BaseResponse pageGroupSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO);

    /**
     * 集团提款审核
     *
     * @param reviewSettleDTO
     * @return
     */
    @PostMapping("/settleorders/reviewGroupSettlement")
    public BaseResponse reviewGroupSettlement(@RequestBody @ApiParam GroupReviewSettleDTO reviewSettleDTO);

    /**
     * 结算交易分页详情
     *
     * @param settleOrderDTO
     * @return
     */
    @PostMapping("/settleorders/pageSettleOrderDetail")
    BaseResponse pageSettleOrderDetail(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO);

    /**
     * 后台管理系统导出详情
     *
     * @param settleOrderDTO
     * @return
     */
    @PostMapping("/settleorders/exportSettleOrder")
    List<SettleOrder> exportSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO);

    @ApiOperation(value = "结算审核")
    @PostMapping("/settleorders/reviewSettlement")
    BaseResponse reviewSettlement(@RequestBody @ApiParam ReviewSettleDTO reviewSettleDTO);

    /**
     * 提款设置
     *
     * @param accountSettleDTO
     * @return
     */
    @PostMapping("/settleorders/updateAccountSettle")
    BaseResponse updateAccountSettle(@RequestBody @ApiParam AccountSettleDTO accountSettleDTO);

    /**
     * 手动提款
     *
     * @param withdrawalDTO
     * @return
     */
    @PostMapping("/settleorders/withdrawal")
    BaseResponse withdrawal(WithdrawalDTO withdrawalDTO);
}
