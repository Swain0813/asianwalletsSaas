package com.asianwallets.base.controller;
import com.asianwallets.base.service.BankCardService;
import com.asianwallets.common.dto.BankCardDTO;
import com.asianwallets.common.dto.BankCardSearchDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.asianwallets.common.base.BaseController;
import java.util.List;

/**
 * <p>
 * 银行卡表 前端控制器
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Api(description = "结算信息管理")
@RestController
@RequestMapping("/bankCard")
public class BankCardController extends BaseController {

    @Autowired
    private BankCardService bankCardService;

    @ApiOperation(value = "添加银行卡信息")
    @PostMapping("/addBankCard")
    public BaseResponse addBankCard(@RequestBody @ApiParam List<BankCardDTO> bankCardDTO) {
        return ResultUtil.success(bankCardService.addBankCard(this.getSysUserVO().getUsername(), bankCardDTO));
    }

    @ApiOperation(value = "修改银行卡信息")
    @PostMapping("/updateBankCard")
    public BaseResponse updateBankCard(@RequestBody @ApiParam BankCardDTO bankCardDTO) {
        return ResultUtil.success(bankCardService.updateBankCard(this.getSysUserVO().getUsername(), bankCardDTO));
    }

    @ApiOperation(value = "根据商户id查询银行卡")
    @GetMapping("/selectBankCardByMerId")
    public BaseResponse selectBankCardByMerId(@RequestParam @ApiParam String merchantId) {
        return ResultUtil.success(bankCardService.selectBankCardByMerId(merchantId));
    }

    @ApiOperation(value = "分页查询银行卡")
    @PostMapping("/pageBankCard")
    public BaseResponse pageBankCard(@RequestBody @ApiParam BankCardSearchDTO bankCardSearchDTO) {
        return ResultUtil.success(bankCardService.pageBankCard(bankCardSearchDTO));
    }

    @ApiOperation(value = "启用禁用银行卡")
    @GetMapping("/banBankCard")
    public BaseResponse banBankCard(@RequestParam @ApiParam String bankCardId, @RequestParam @ApiParam Boolean enabled) {
        return ResultUtil.success(bankCardService.banBankCard(this.getSysUserVO().getUsername(), bankCardId, enabled));
    }

    @ApiOperation(value = "设置默认银行卡")
    @GetMapping("/defaultBankCard")
    public BaseResponse defaultBankCard(@RequestParam @ApiParam String bankCardId, @RequestParam @ApiParam Boolean defaultFlag) {
        return ResultUtil.success(bankCardService.defaultBankCard(this.getSysUserVO().getUsername(), bankCardId, defaultFlag));
    }


}
