package com.asianwallets.base.controller;

import com.asianwallets.base.service.BankService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.ExportBankVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(description = "银行接口")
@RequestMapping("/bank")
public class BankController extends BaseController {

    @Autowired
    private BankService bankService;

    @ApiOperation(value = "增加银行")
    @PostMapping("/addBank")
    public BaseResponse addBank(@RequestBody @ApiParam BankDTO bankDTO) {
        return ResultUtil.success(bankService.addBank(getSysUserVO().getUsername(), bankDTO));
    }

    @ApiOperation(value = "修改银行信息")
    @PostMapping("/updateBank")
    public BaseResponse updateBank(@RequestBody @ApiParam BankDTO bankDTO) {
        return ResultUtil.success(bankService.updateBank(getSysUserVO().getUsername(), bankDTO));
    }

    @ApiOperation(value = "分页查询银行信息")
    @PostMapping("/pageFindBank")
    public BaseResponse pageFindBank(@RequestBody @ApiParam BankDTO bankDTO) {
        return ResultUtil.success(bankService.pageFindBank(bankDTO));
    }

    @ApiOperation(value = "导出银行信息")
    @PostMapping("/exportBank")
    public List<ExportBankVO> exportBank(@RequestBody @ApiParam BankDTO bankDTO) {
        return bankService.exportBank(bankDTO);
    }

    @ApiOperation(value = "导入银行信息")
    @PostMapping("/importBank")
    public BaseResponse importBank(@RequestBody @ApiParam List<Bank> bankList) {
        return ResultUtil.success(bankService.importBank(bankList));
    }

    @ApiOperation(value = "根据银行名称与币种查询启用银行")
    @PostMapping("/getByBankNameAndCurrency")
    public Bank getByBankNameAndCurrency(@RequestBody @ApiParam BankDTO bankDTO) {
        return bankService.getByBankNameAndCurrency(bankDTO);
    }
}
