package com.asianwallets.channels.controller;
import com.asianwallets.channels.service.UpiService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.upi.UpiDTO;
import com.asianwallets.common.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-04 14:34
 **/
@RestController
@Api(description = "银联")
@RequestMapping("/upi")
public class UpiController extends BaseController {

    @Autowired
    private UpiService upiService;


    @ApiOperation("upiPay")
    @PostMapping("/upiPay")
    public BaseResponse upiPay(@RequestBody @ApiParam UpiDTO upiDTO) {
        return upiService.upiPay(upiDTO);
    }

    @ApiOperation("upiBankPay")
    @PostMapping("/upiBankPay")
    public BaseResponse upiBankPayPay(@RequestBody @ApiParam UpiDTO upiDTO) {
        return upiService.upiBankPay(upiDTO);
    }

    @ApiOperation("upiRefund")
    @PostMapping("/upiRefund")
    public BaseResponse upiRefund(@RequestBody @ApiParam UpiDTO upiDTO) {
        return upiService.upiRefund(upiDTO);
    }

    @ApiOperation("upiCancel")
    @PostMapping("/upiCancel")
    public BaseResponse upiCancel(@RequestBody @ApiParam UpiDTO upiDTO) {
        return upiService.upiCancel(upiDTO);
    }

    @ApiOperation("upiQuery")
    @PostMapping("/upiQuery")
    public BaseResponse upiQuery(@RequestBody @ApiParam UpiDTO upiDTO) {
        return upiService.upiQuery(upiDTO);
    }



}
