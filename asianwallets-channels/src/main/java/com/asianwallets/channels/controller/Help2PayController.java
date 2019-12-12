package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.Help2PayService;
import com.asianwallets.common.dto.help2pay.Help2PayOutDTO;
import com.asianwallets.common.dto.help2pay.Help2PayRequestDTO;
import com.asianwallets.common.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-06-10 10:01
 **/
@RestController
@Api(description = "HELP2PAY")
@RequestMapping("/help")
public class Help2PayController {

    @Autowired
    private Help2PayService help2PayService;

    @ApiOperation(value = "HELP2PAY收单接口")
    @PostMapping("help2pay")
    public BaseResponse help2pay(@RequestBody @ApiParam @Valid Help2PayRequestDTO help2PayRequestDTO) {
        return help2PayService.help2Pay(help2PayRequestDTO);
    }


    @ApiOperation(value = "HELP2PAY汇款接口")
    @PostMapping("help2PayOut")
    public BaseResponse help2PayOut(@RequestBody @ApiParam @Valid Help2PayOutDTO help2PayOutDTO) {
        return help2PayService.help2PayOut(help2PayOutDTO);
    }


}
