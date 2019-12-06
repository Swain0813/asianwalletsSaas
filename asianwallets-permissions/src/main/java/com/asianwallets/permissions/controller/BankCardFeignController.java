package com.asianwallets.permissions.controller;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.BankCardDTO;
import com.asianwallets.common.dto.BankCardSearchDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.BankCardFeign;
import com.asianwallets.permissions.service.OperationLogService;
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

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "添加银行卡信息")
    @PostMapping("/addBankCard")
    public BaseResponse addBankCard(@RequestBody @ApiParam List<BankCardDTO> bankCardDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSONArray.toJSONString(bankCardDTO),
                "添加银行卡信息"));
        return bankCardFeign.addBankCard(bankCardDTO);
    }

    @ApiOperation(value = "修改银行卡信息")
    @PostMapping("/updateBankCard")
    public BaseResponse updateBankCard(@RequestBody @ApiParam BankCardDTO bankCardDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSONArray.toJSONString(bankCardDTO),
                "修改银行卡信息"));
        return bankCardFeign.updateBankCard(bankCardDTO);
    }

    @ApiOperation(value = "根据商户id查询银行卡")
    @GetMapping("/selectBankCardByMerId")
    public BaseResponse selectBankCardByMerId(@RequestParam @ApiParam String merchantId) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSONObject.toJSONString(this.getRequest().getParameterMap()),
                "根据机构id查询银行卡"));
        return bankCardFeign.selectBankCardByMerId(merchantId);
    }

    @ApiOperation(value = "分页查询银行卡")
    @PostMapping("/pageBankCard")
    public BaseResponse pageBankCard(@RequestBody @ApiParam BankCardSearchDTO bankCardSearchDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSONArray.toJSONString(bankCardSearchDTO),
                "分页查询银行卡"));
        return bankCardFeign.pageBankCard(bankCardSearchDTO);
    }

    @ApiOperation(value = "启用禁用银行卡")
    @GetMapping("/banBankCard")
    public BaseResponse banBankCard(@RequestParam @ApiParam String bankCardId, @RequestParam @ApiParam Boolean enabled) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(this.getRequest().getParameterMap()),
                "启用禁用银行卡"));
        return bankCardFeign.banBankCard(bankCardId, enabled);
    }

    @ApiOperation(value = "设置默认银行卡")
    @GetMapping("/defaultBankCard")
    public BaseResponse defaultBankCard(@RequestParam @ApiParam String bankCardId, @RequestParam @ApiParam Boolean defaultFlag) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(this.getRequest().getParameterMap()),
                "设置默认银行卡"));
        return bankCardFeign.defaultBankCard(bankCardId, defaultFlag);
    }
}
