package com.asianwallets.base.controller;

import com.asianwallets.base.service.PayTypeService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.PayTypeDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CurrencyController
 * @Description 支付方式
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/paytype")
@Api(description = "支付方式接口")
public class PayTypeController extends BaseController {

    @Autowired
    private PayTypeService PayTypeService;

    @ApiOperation(value = "新增支付方式")
    @PostMapping("addPayType")
    public BaseResponse addPaytype(@RequestBody @ApiParam PayTypeDTO PayTypeDTO) {
        PayTypeDTO.setCreator(this.getUserName().getUsername());
        return ResultUtil.success(PayTypeService.addPayType(PayTypeDTO));
    }

    @ApiOperation(value = "修改支付方式")
    @PostMapping("updatePayType")
    public BaseResponse updatePaytype(@RequestBody @ApiParam PayTypeDTO PayTypeDTO) {
        PayTypeDTO.setLanguage(this.getLanguage());
        PayTypeDTO.setModifier(this.getUserName().getUsername());
        return ResultUtil.success(PayTypeService.updatePayType(PayTypeDTO));
    }

    @ApiOperation(value = "查询支付方式")
    @PostMapping("pagePayType")
    public BaseResponse pagePaytype(@RequestBody @ApiParam PayTypeDTO PayTypeDTO) {
        return ResultUtil.success(PayTypeService.pagePayType(PayTypeDTO));
    }

    @ApiOperation(value = "启用禁用支付方式")
    @PostMapping("banPayType")
    public BaseResponse banCurrency(@RequestBody @ApiParam PayTypeDTO PayTypeDTO) {
        PayTypeDTO.setModifier(this.getUserName().getUsername());
        PayTypeDTO.setLanguage(this.getLanguage());
        return ResultUtil.success(PayTypeService.banPayType(PayTypeDTO));
    }

    @ApiOperation(value = "查询所有支付方式")
    @GetMapping("inquireAllPayType")
    public BaseResponse inquireAllPaytype() {
        return ResultUtil.success(PayTypeService.inquireAllPaytype(this.getLanguage()));
    }
}
