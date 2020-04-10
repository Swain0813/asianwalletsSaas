package com.asianwallets.base.controller;
import com.asianwallets.base.service.SettleCheckAccountService;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.dto.TradeCheckAccountSettleExportDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.DateToolUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.asianwallets.common.base.BaseController;
import java.util.Map;

@Api(description = "商户结算对账单")
@RestController
@RequestMapping("/settleCheckAccount")
public class SettleCheckAccountController extends BaseController {

    @Autowired
    private SettleCheckAccountService settleCheckAccountService;


    @ApiOperation(value = "查询前一天所有结算记录")
    @GetMapping("selectTcsStFlow")
    public BaseResponse selectTcsStFlow(@RequestParam @ApiParam String time) {
        return ResultUtil.success(settleCheckAccountService.settleAccountCheck(DateToolUtils.getDateByStr(time)));
    }

    @ApiOperation(value = "分页查询商户结算对账")
    @PostMapping("pageSettleAccountCheck")
    public BaseResponse pageSettleAccountCheck(@RequestBody @ApiParam TradeCheckAccountDTO tradeCheckAccountDTO) {
        return ResultUtil.success(settleCheckAccountService.pageSettleAccountCheck(tradeCheckAccountDTO));
    }

    @ApiOperation(value = "分页查询商户结算对账详情")
    @PostMapping("pageSettleAccountCheckDetail")
    public BaseResponse pageSettleAccountCheckDetail(@RequestBody @ApiParam TradeCheckAccountDTO tradeCheckAccountDTO) {
        return ResultUtil.success(settleCheckAccountService.pageSettleAccountCheckDetail(tradeCheckAccountDTO));
    }

    @ApiOperation(value = "导出商户结算对账单")
    @PostMapping("exportSettleAccountCheck")
    public Map<String, Object> exportSettleAccountCheck(@RequestBody @ApiParam TradeCheckAccountSettleExportDTO tradeCheckAccountDTO) {
        return settleCheckAccountService.exportSettleAccountCheck(tradeCheckAccountDTO);
    }


}
