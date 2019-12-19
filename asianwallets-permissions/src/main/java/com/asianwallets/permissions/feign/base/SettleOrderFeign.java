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

}
