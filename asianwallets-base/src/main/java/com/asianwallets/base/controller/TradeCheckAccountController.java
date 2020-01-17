package com.asianwallets.base.controller;

import com.asianwallets.base.service.TradeCheckAccountService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.ExportTradeAccountVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

    @ApiOperation(value = "分页查询交易对账总表信息")
    @PostMapping("/pageFindTradeCheckAccount")
    public BaseResponse pageFindTradeCheckAccount(@RequestBody @ApiParam @Valid TradeCheckAccountDTO tradeCheckAccountDTO) {
        return ResultUtil.success(tradeCheckAccountService.pageFindTradeCheckAccount(tradeCheckAccountDTO));
    }

    @ApiOperation(value = "分页查询交易对账详细表信息")
    @PostMapping("/pageFindTradeCheckAccountDetail")
    public BaseResponse pageFindTradeCheckAccountDetail(@RequestBody @ApiParam @Valid TradeCheckAccountDTO tradeCheckAccountDTO) {
        return ResultUtil.success(tradeCheckAccountService.pageFindTradeCheckAccountDetail(tradeCheckAccountDTO));
    }


    @ApiOperation(value = "导出商户交易对账单")
    @PostMapping("exportTradeCheckAccount")
    public ExportTradeAccountVO exportTradeCheckAccount(@RequestBody @ApiParam @Valid TradeCheckAccountDTO tradeCheckAccountDTO) {
        return tradeCheckAccountService.exportTradeCheckAccount(tradeCheckAccountDTO);
    }
}
