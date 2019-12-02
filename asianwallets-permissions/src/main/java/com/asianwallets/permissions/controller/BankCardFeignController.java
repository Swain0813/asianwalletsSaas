package com.asianwallets.permissions.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.BankCardDTO;
import com.asianwallets.common.dto.BankCardSearchDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.base.BankCardFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 结算信息管理
 * @author: YangXu
 * @create: 2019-12-02 10:59
 **/

@RestController
@Api(description = "结算信息管理")
@RequestMapping("/bankCard")
public class BankCardFeignController extends BaseController {

    @Autowired
    private BankCardFeign bankCardFeign;


    @ApiOperation(value = "添加银行卡信息")
    @PostMapping("/addBankCard")
    public BaseResponse addBankCard(@RequestBody @ApiParam List<BankCardDTO> bankCardDTO) {
        return bankCardFeign.addBankCard(bankCardDTO);
    }

    @ApiOperation(value = "修改银行卡信息")
    @PostMapping("/updateBankCard")
    public BaseResponse updateBankCard(@RequestBody @ApiParam BankCardDTO bankCardDTO) {
        return bankCardFeign.updateBankCard(bankCardDTO);
    }

    @ApiOperation(value = "根据商户id查询银行卡")
    @GetMapping("/selectBankCardByMerId")
    public BaseResponse selectBankCardByMerId(@RequestParam @ApiParam String merchantId) {
        return bankCardFeign.selectBankCardByMerId(merchantId);
    }

    @ApiOperation(value = "分页查询银行卡")
    @PostMapping("/pageBankCard")
    public BaseResponse pageBankCard(@RequestBody @ApiParam BankCardSearchDTO bankCardSearchDTO) {
        return bankCardFeign.pageBankCard(bankCardSearchDTO);
    }

    @ApiOperation(value = "启用禁用银行卡")
    @GetMapping("/banBankCard")
    public BaseResponse banBankCard(@RequestParam @ApiParam String bankCardId, @RequestParam @ApiParam Boolean enabled) {
        return bankCardFeign.banBankCard(bankCardId, enabled);
    }

    @ApiOperation(value = "设置默认银行卡")
    @GetMapping("/defaultBankCard")
    public BaseResponse defaultBankCard(@RequestParam @ApiParam String bankCardId, @RequestParam @ApiParam Boolean defaultFlag) {
        return bankCardFeign.defaultBankCard(bankCardId, defaultFlag);
    }
}
