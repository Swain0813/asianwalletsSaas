package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.Ad3Service;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.ad3.AD3CSBScanPayDTO;
import com.asianwallets.common.dto.ad3.AD3ONOFFRefundDTO;
import com.asianwallets.common.dto.ad3.AD3OnlineAcquireDTO;
import com.asianwallets.common.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "AD3")
@RequestMapping("/ad3")
public class Ad3Controller extends BaseController {

    @Autowired
    private Ad3Service ad3Service;

    @ApiOperation(value = "AD3线下CSB接口")
    @PostMapping("/offlineCsb")
    public BaseResponse offlineCsb(@RequestBody @ApiParam AD3CSBScanPayDTO ad3CSBScanPayDTO) {
        return ad3Service.offlineCsb(ad3CSBScanPayDTO);
    }

    @ApiOperation(value = "AD3线下退款接口")
    @PostMapping("/offlineRefund")
    public BaseResponse offlineRefund(@RequestBody @ApiParam AD3ONOFFRefundDTO ad3RefundDTO) {
        return ad3Service.offlineRefund(ad3RefundDTO);
    }

    @ApiOperation(value = "AD3线上退款接口")
    @PostMapping("/onlineRefund")
    public BaseResponse onlineRefund(@RequestBody @ApiParam AD3ONOFFRefundDTO sendAdRefundDTO) {
        return ad3Service.onlineRefund(sendAdRefundDTO);
    }

    @ApiOperation(value = "AD3线上收款接口")
    @PostMapping("/onlinePay")
    public BaseResponse onlinePay(@RequestBody @ApiParam AD3OnlineAcquireDTO ad3OnlineAcquireDTO) {
        return ad3Service.onlinePay(ad3OnlineAcquireDTO);
    }
}
