package com.asianwallets.trade.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.trade.dto.CsbDynamicScanDTO;
import com.asianwallets.trade.service.OfflineTradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(description = "线下交易接口")
@RequestMapping("/offline")
public class OfflineTradeController extends BaseController {

    @Autowired
    private OfflineTradeService offlineTradeService;

    @ApiOperation(value = "线下同机构CSB动态扫码")
    @PostMapping("csbDynamicScan")
    public BaseResponse csbDynamicScan(@RequestBody @ApiParam @Valid CsbDynamicScanDTO csbDynamicScanDTO) {
        BaseResponse baseResponse = offlineTradeService.csbDynamicScan(csbDynamicScanDTO);
        String code = baseResponse.getCode();
        if (StringUtils.isEmpty(code)) {
            baseResponse.setCode(EResultEnum.SUCCESS.getCode());
            baseResponse.setMsg("SUCCESS");
            return baseResponse;
        }
        //code不为空 msg不为空 返回通道的错误信息
        if (!StringUtils.isEmpty(baseResponse.getMsg())) {
            return baseResponse;
        }
        return ResultUtil.error(code, getErrorMsgMap(code));
    }
}

