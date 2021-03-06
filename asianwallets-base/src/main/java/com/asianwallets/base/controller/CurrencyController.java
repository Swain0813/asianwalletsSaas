package com.asianwallets.base.controller;
import com.asianwallets.base.service.CurrencyService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.CurrencyDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.CurrencyExportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/currency")
@Api(description = "币种接口")
public class CurrencyController extends BaseController {

    @Autowired
    private CurrencyService currencyService;

    @ApiOperation(value = "新增币种")
    @PostMapping("addCurrency")
    public BaseResponse addCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        currencyDTO.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(currencyService.addCurrency(currencyDTO));
    }

    @ApiOperation(value = "修改币种")
    @PostMapping("updateCurrency")
    public BaseResponse updateCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        currencyDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(currencyService.updateCurrency(currencyDTO));
    }

    @ApiOperation(value = "查询币种")
    @PostMapping("pageCurrency")
    public BaseResponse pageCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        return ResultUtil.success(currencyService.pageCurrency(currencyDTO));
    }

    @ApiOperation(value = "启用禁用币种")
    @PostMapping("banCurrency")
    public BaseResponse banCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        currencyDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(currencyService.banCurrency(currencyDTO));
    }

    @ApiOperation(value = "查询所有币种")
    @GetMapping("inquireAllCurrency")
    public BaseResponse inquireAllCurrency() {
        return ResultUtil.success(currencyService.inquireAllCurrency());
    }

    @ApiOperation(value = "导出币种信息")
    @PostMapping("exportCurrency")
    public List<CurrencyExportVO> exportCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO){
        return currencyService.exportCurrency(currencyDTO);
    }
}
