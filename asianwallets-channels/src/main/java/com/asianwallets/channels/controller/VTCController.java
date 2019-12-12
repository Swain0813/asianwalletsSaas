package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.VTCService;
import com.asianwallets.common.dto.vtc.VTCRequestDTO;
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
 * @create: 2019-05-30 17:43
 **/
@RestController
@Api(description = "vtcPay")
@RequestMapping("/vtc")
public class VTCController {

    @Autowired
    private VTCService vtcService;

    @ApiOperation(value = "vtcPay收单接口")
    @PostMapping("vtcPay")
    public BaseResponse vtcPay(@RequestBody @ApiParam @Valid VTCRequestDTO vtcRequestDTO) {
        return vtcService.vtcPay(vtcRequestDTO);
    }

}
