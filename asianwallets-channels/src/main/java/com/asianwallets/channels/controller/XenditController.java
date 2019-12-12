package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.XenditService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.xendit.XenditDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @description: xendit
 * @author: XuWenQi
 * @create: 2019-06-19 11:26
 **/
@RestController
@Api(description = "xendit通道")
@RequestMapping("/xendit")
public class XenditController extends BaseController {

    @Autowired
    private XenditService xenditService;

    @ApiOperation(value = "xendit收单接口")
    @PostMapping("xenditPay")
    public BaseResponse xenditPay(@RequestBody @ApiParam @Valid XenditDTO xenditDTO) {
        return xenditService.xenditPay(xenditDTO);
    }

    @ApiOperation(value = "创建一个虚拟账户")
    @GetMapping("creatVirtualAccounts")
    public BaseResponse creatVirtualAccounts(@RequestParam @ApiParam @Valid String bankCode, String apiKey, String bankName) {
        return ResultUtil.success(xenditService.creatVirtualAccounts(bankCode, apiKey, bankName));
    }

}
