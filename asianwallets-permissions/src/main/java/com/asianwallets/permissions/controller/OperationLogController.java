package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.OperationLogDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志相关业务
 */
@RestController
@Api(description ="操作相关接口服务")
@RequestMapping("/operatlog")
public class OperationLogController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "查询所有操作日志")
    @PostMapping("/pageOperationLog")
    public BaseResponse pageOperationLog(@RequestBody @ApiParam OperationLogDTO operationLogDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(operationLogDTO),
                "查询所有操作日志"));
        return ResultUtil.success(operationLogService.pageOperationLog(operationLogDTO));
    }
}
