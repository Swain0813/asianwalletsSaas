package com.asianwallets.permissions.controller;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.trade.RefundTradeFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "退款相关接口")
@RequestMapping("/trade")
public class RefundTradeFeignController extends BaseController {

    @Autowired
    private RefundTradeFeign refundTradeFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "人工退款接口")
    @GetMapping("/artificialRefund")
    public BaseResponse artificialRefund(@RequestParam @ApiParam String refundOrderId, Boolean enabled, String remark) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(this.getRequest().getParameterMap()),
                "人工退款接口"));
        return refundTradeFeign.artificialRefund(refundOrderId,enabled,remark);
    }
}
