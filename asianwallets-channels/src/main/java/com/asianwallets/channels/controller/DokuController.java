package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.DokuService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.doku.DOKUReqDTO;
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
 * @description: Doku通道
 * @author: XuWenQi
 * @create: 2019-11-12 14:14
 **/
@RestController
@Api(description = "Doku通道")
@RequestMapping("/doku")
public class DokuController extends BaseController {

    @Autowired
    private DokuService dokuService;

    @ApiOperation("Doku收单接口")
    @PostMapping("/payMent")
    public BaseResponse payMent(@RequestBody @ApiParam DOKUReqDTO dokuReqDTO) {
        return dokuService.payMent(dokuReqDTO);
    }

    @ApiOperation("Doku查询接口")
    @PostMapping("/checkStatus")
    public BaseResponse checkStatus(@RequestBody @ApiParam DOKUReqDTO dokuReqDTO) {
        return dokuService.checkStatus(dokuReqDTO);
    }

    @ApiOperation("Doku退款接口")
    @PostMapping("/refund")
    public BaseResponse refund(@RequestBody @ApiParam DOKUReqDTO dokuReqDTO) {
        return dokuService.refund(dokuReqDTO);
    }
}
