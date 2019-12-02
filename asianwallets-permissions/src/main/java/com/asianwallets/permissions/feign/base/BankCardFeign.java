package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.BankCardDTO;
import com.asianwallets.common.dto.BankCardSearchDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.BankCardFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "asianwallets-base", fallback = BankCardFeignImpl.class)
public interface BankCardFeign {

    @ApiOperation(value = "添加银行卡信息")
    @PostMapping("/bankCard/addBankCard")
    BaseResponse addBankCard(@RequestBody @ApiParam List<BankCardDTO> bankCardDTO);


    @ApiOperation(value = "修改银行卡信息")
    @PostMapping("/bankCard/updateBankCard")
    BaseResponse updateBankCard(@RequestBody @ApiParam BankCardDTO bankCardDTO);

    @ApiOperation(value = "根据商户id查询银行卡")
    @GetMapping("/bankCard/selectBankCardByMerId")
    BaseResponse selectBankCardByMerId(@RequestParam("merchantId") @ApiParam String merchantId);

    @ApiOperation(value = "分页查询银行卡")
    @PostMapping("/bankCard/pageBankCard")
    BaseResponse pageBankCard(@RequestBody @ApiParam BankCardSearchDTO bankCardSearchDTO);

    @ApiOperation(value = "启用禁用银行卡")
    @GetMapping("/bankCard/banBankCard")
    BaseResponse banBankCard(@RequestParam("bankCardId") @ApiParam String bankCardId, @RequestParam("enabled") @ApiParam Boolean enabled);

    @ApiOperation(value = "设置默认银行卡")
    @GetMapping("/bankCard/defaultBankCard")
    BaseResponse defaultBankCard(@RequestParam("bankCardId") @ApiParam String bankCardId, @RequestParam("defaultFlag") @ApiParam Boolean defaultFlag);

}
