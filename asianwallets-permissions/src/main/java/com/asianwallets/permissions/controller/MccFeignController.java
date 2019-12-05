package com.asianwallets.permissions.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.base.MccFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName MCC
 * @Description MCC
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/mcc")
@Api(description = "MCC")
public class MccFeignController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private MccFeign mccFeign;

    @ApiOperation(value = "查询所有MCC")
    @GetMapping("inquireAllCountry")
    public BaseResponse inquireAllCountry() {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, null,
                "查询所有MCC"));
        return ResultUtil.success(mccFeign.inquireAllMcc());
    }
}
