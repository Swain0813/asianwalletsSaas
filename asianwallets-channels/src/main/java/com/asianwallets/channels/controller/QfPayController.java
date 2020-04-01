package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.QfPayService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.qfpay.QfPayDTO;
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
 * @create: 2020-02-11 11:32
 **/
@RestController
@Api(description = "QFPay通道")
@RequestMapping("/qfPay")
public class QfPayController extends BaseController {

    @Autowired
    private QfPayService qfPayService;

    @ApiOperation("qfPayCSB")
    @PostMapping("/qfPayCSB")
    public BaseResponse qfPayCSB(@RequestBody @ApiParam QfPayDTO qfPayDTO) {
        return qfPayService.qfPayCSB(qfPayDTO);
    }

    @ApiOperation("qfPayBSC")
    @PostMapping("/qfPayBSC")
    public BaseResponse qfPayBSC(@RequestBody @ApiParam QfPayDTO qfPayDTO) {
        return qfPayService.qfPayBSC(qfPayDTO);
    }

    @ApiOperation("qfPayQuery")
    @PostMapping("/qfPayQuery")
    public BaseResponse qfPayQuery(@RequestBody @ApiParam QfPayDTO qfPayDTO) {
        return qfPayService.qfPayQuery(qfPayDTO);
    }

    @ApiOperation("qfPayRefund")
    @PostMapping("/qfPayRefund")
    public BaseResponse qfPayRefund(@RequestBody @ApiParam QfPayDTO qfPayDTO) {
        return qfPayService.qfPayRefund(qfPayDTO);
    }


    @ApiOperation("qfPayRefundSearch")
    @PostMapping("/qfPayRefundSearch")
    public BaseResponse qfPayRefundSearch(@RequestBody @ApiParam QfPayDTO qfPayDTO) {
        return qfPayService.qfPayRefundSearch(qfPayDTO);
    }


}
