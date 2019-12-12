package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.EghlService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.eghl.EGHLRequestDTO;
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
 * @description: EGHL
 * @author: YangXu
 * @create: 2019-05-28 14:14
 **/
@RestController
@Api(description = "EGHL")
@RequestMapping("/eghl")
public class EghlController extends BaseController {

    @Autowired
    private EghlService eghlService;

    @ApiOperation(value = "eghl收单接口")
    @PostMapping("eGHLPay")
    public BaseResponse eGHLPay(@RequestBody @ApiParam @Valid EGHLRequestDTO eghlRequestDTO) {
        return eghlService.eGHLPay(eghlRequestDTO);
    }
}
