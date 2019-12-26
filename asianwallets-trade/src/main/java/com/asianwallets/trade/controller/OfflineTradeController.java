package com.asianwallets.trade.controller;

import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.trade.dto.OfflineCheckOrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.trade.dto.OfflineLoginDTO;
import com.asianwallets.trade.dto.OfflineTradeDTO;
import com.asianwallets.trade.dto.PosGetMerProDTO;
import com.asianwallets.trade.service.OfflineTradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(description = "线下交易接口")
@RequestMapping("/offline")
public class OfflineTradeController extends BaseController {

    @Autowired
    private OfflineTradeService offlineTradeService;

    @ApiOperation(value = "线下登录")
    @PostMapping("/login")
    public BaseResponse login(@RequestBody @ApiParam @Valid OfflineLoginDTO offlineLoginDTO) {
        String token = offlineTradeService.login(offlineLoginDTO);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);
        return ResultUtil.success(jsonObject);
    }

    @ApiOperation(value = "线下同机构CSB动态扫码")
    @PostMapping("csbDynamicScan")
    public BaseResponse csbDynamicScan(@RequestBody @ApiParam @Valid OfflineTradeDTO offlineTradeDTO) {
        return ResultUtil.success(offlineTradeService.csbDynamicScan(offlineTradeDTO));
    }

    @ApiOperation(value = "线下同机构BSC动态扫码")
    @PostMapping("bscDynamicScan")
    public BaseResponse bscDynamicScan(@RequestBody @ApiParam @Valid OfflineTradeDTO offlineTradeDTO) {
        return ResultUtil.success(offlineTradeService.bscDynamicScan(offlineTradeDTO));
    }

    @ApiOperation(value = "线下查询订单列表【对外API】")
    @PostMapping("checkOrder")
    public BaseResponse checkOrder(@RequestBody @ApiParam @Valid OfflineCheckOrdersDTO offlineCheckOrdersDTO) {
        return ResultUtil.success(offlineTradeService.checkOrder(offlineCheckOrdersDTO));
    }

    @ApiOperation(value = "POS机查询商户产品,币种信息")
    @PostMapping("posGetMerPro")
    public BaseResponse posGetMerPro(@RequestBody @ApiParam @Valid PosGetMerProDTO posGetMerProDTO) {
        return ResultUtil.success(offlineTradeService.posGetMerPro(posGetMerProDTO));
    }
}

