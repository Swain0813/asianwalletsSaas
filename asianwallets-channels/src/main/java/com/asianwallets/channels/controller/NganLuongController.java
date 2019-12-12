package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.NganLuongService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.nganluong.NganLuongDTO;
import com.asianwallets.common.dto.nganluong.NganLuongQueryDTO;
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
 * @description: NganLuong
 * @author: YangXu
 * @create: 2019-06-18 11:12
 **/
@RestController
@Api(description = "NganLuong")
@RequestMapping("/nganLuong")
public class NganLuongController extends BaseController {

    @Autowired
    private NganLuongService nganLuongService;

    @ApiOperation(value = "NganLuong收单接口")
    @PostMapping("/nganLuongPay")
    public BaseResponse nganLuongPay(@RequestBody @ApiParam @Valid NganLuongDTO nganLuongDTO) {
        return nganLuongService.nganLuongPay(nganLuongDTO);
    }

    @ApiOperation(value = "NganLuong查询接口")
    @PostMapping("/nganLuongQuery")
    public BaseResponse nganLuongQuery(@RequestBody @ApiParam @Valid NganLuongQueryDTO nganLuongQueryDTO) {
        return nganLuongService.nganLuongQuery(nganLuongQueryDTO);
    }
}
