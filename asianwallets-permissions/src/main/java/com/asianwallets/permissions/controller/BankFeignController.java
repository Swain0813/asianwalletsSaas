package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.ExportBankVO;
import com.asianwallets.permissions.feign.base.BankFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(description = "银行接口")
@RequestMapping("/bank")
public class BankFeignController extends BaseController {

    @Autowired
    private BankFeign bankFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "增加银行")
    @PostMapping("/addBank")
    public BaseResponse addBank(@RequestBody @ApiParam BankDTO bankDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(bankDTO),
                "增加银行"));
        return bankFeign.addBank(bankDTO);
    }

    @ApiOperation(value = "修改银行信息")
    @PostMapping("/updateBank")
    public BaseResponse updateBank(@RequestBody @ApiParam BankDTO bankDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(bankDTO),
                "修改银行信息"));
        return bankFeign.updateBank(bankDTO);
    }

    @ApiOperation(value = "分页查询银行信息")
    @PostMapping("/pageFindBank")
    public BaseResponse pageFindBank(@RequestBody @ApiParam BankDTO bankDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(bankDTO),
                "分页查询银行信息"));
        return bankFeign.pageFindBank(bankDTO);
    }

    @ApiOperation(value = "导出银行信息")
    @PostMapping("/exportBank")
    public List<ExportBankVO> exportBank(@RequestBody @ApiParam BankDTO bankDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(bankDTO),
                "导出银行信息"));
        return bankFeign.exportBank(bankDTO);
    }
}
