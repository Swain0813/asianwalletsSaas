package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.THService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.dto.th.ISO8583.ThDTO;
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
@Api(description = "通华")
@RequestMapping("/th")
public class ThController extends BaseController {

    @Autowired
    private THService thService;

    @ApiOperation("thCSB")
    @PostMapping("/thCSB")
    public BaseResponse thCSB(@RequestBody @ApiParam ThDTO thDTO) {
        return thService.thCSB(thDTO);
    }

    @ApiOperation("thBSC")
    @PostMapping("/thBSC")
    public BaseResponse thBSC(@RequestBody @ApiParam ThDTO thDTO) {
        return thService.thBSC(thDTO);
    }

    @ApiOperation("thRefund")
    @PostMapping("/thRefund")
    public BaseResponse thRefund(@RequestBody @ApiParam ThDTO thDTO) {
        return thService.thRefund(thDTO);
    }
    @ApiOperation("thQuery")
    @PostMapping("/thQuery")
    public BaseResponse thQuery(@RequestBody @ApiParam ThDTO thDTO) {
        return thService.thQuery(thDTO);
    }

}
