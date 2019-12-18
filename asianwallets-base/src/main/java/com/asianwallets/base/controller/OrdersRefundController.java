package com.asianwallets.base.controller;

import com.asianwallets.base.service.OrdersRefundService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.OrdersRefundVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(description = "退款订单接口")
@RequestMapping("/ordersRefund")
public class OrdersRefundController extends BaseController {
    @Autowired
    private OrdersRefundService ordersRefundService;

    @ApiOperation(value = "分页查询退款订单信息")
    @PostMapping("pageFindOrdersRefund")
    public BaseResponse pageFindOrdersRefund(@RequestBody @ApiParam OrdersRefundDTO ordersRefundDTO) {
        return ResultUtil.success(ordersRefundService.pageFindOrdersRefund(ordersRefundDTO));
    }

    @ApiOperation(value = "查询退款订单详情信息")
    @GetMapping("getOrdersRefundDetail")
    public BaseResponse getOrdersRefundDetail(@RequestParam("refundId") @ApiParam String refundId) {
        return ResultUtil.success(ordersRefundService.getOrdersRefundDetail(refundId));
    }

    @ApiOperation(value = "退款单导出")
    @PostMapping("exportOrdersRefund")
    public List<OrdersRefundVO> exportOrdersRefund(@RequestBody @ApiParam OrdersRefundDTO ordersRefundDTO) {
        return ordersRefundService.exportOrdersRefund(ordersRefundDTO);
    }
}
