package com.asianwallets.base.controller;

import com.asianwallets.base.service.TradeCheckAccountService;
import com.asianwallets.common.base.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "商户交易对账单")
@RestController
@RequestMapping("/base")
public class TradeCheckAccountController extends BaseController {

    @Autowired
    private TradeCheckAccountService tradeCheckAccountService;

    @ApiOperation(value = "生成昨日商户交易对账单")
    @PostMapping("/tradeCheckAccount")
    public void tradeCheckAccount() {
        tradeCheckAccountService.tradeCheckAccount();
    }

}
