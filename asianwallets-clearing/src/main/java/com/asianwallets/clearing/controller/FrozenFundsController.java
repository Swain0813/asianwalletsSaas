package com.asianwallets.clearing.controller;
import com.asianwallets.clearing.service.FrozenFundsService;
import com.asianwallets.common.vo.clearing.CSFrozenFundsRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 资金冻结/解冻接口（异常资金）
 * @author: YangXu
 * @create: 2019-07-25 11:33
 **/
@RestController
@Api(description = "资金冻结/解冻接口")
@RequestMapping("/FrozenFundsAction")
public class FrozenFundsController {


    @Autowired
    private FrozenFundsService frozenFundsService;

    @ApiOperation(value = "资金冻结/解冻接口")
    @PostMapping("/CSFrozenFunds")
    public CSFrozenFundsRequest CSFrozenFunds(@RequestBody @ApiParam CSFrozenFundsRequest ffr) {
        return frozenFundsService.CSFrozenFunds(ffr);
    }

}
