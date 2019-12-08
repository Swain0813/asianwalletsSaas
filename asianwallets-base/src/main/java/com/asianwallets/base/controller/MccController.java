package com.asianwallets.base.controller;

import com.asianwallets.base.service.MccService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.MccDTO;
import com.asianwallets.common.entity.Mcc;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName mcc
 * @Description mcc
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/mcc")
@Api("mcc")
public class MccController extends BaseController {

    @Autowired
    private MccService mccService;

    @ApiOperation(value = "新增mcc")
    @PostMapping("addMcc")
    public BaseResponse addMcc(@RequestBody @ApiParam MccDTO mccDto) {
        mccDto.setCreator(this.getSysUserVO().getUsername());
        mccDto.setLanguage(this.getLanguage());
        return ResultUtil.success(mccService.addMcc(mccDto));
    }

    @ApiOperation(value = "修改mcc")
    @PostMapping("updateMcc")
    public BaseResponse updateMcc(@RequestBody @ApiParam MccDTO mccDto) {
        mccDto.setLanguage(this.getLanguage());
        mccDto.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(mccService.updateMcc(mccDto));
    }

    @ApiOperation(value = "查询mcc")
    @PostMapping("pageMcc")
    public BaseResponse pageMcc(@RequestBody @ApiParam MccDTO mccDto) {
        mccDto.setLanguage(this.getLanguage());
        return ResultUtil.success(mccService.pageMcc(mccDto));
    }

    @ApiOperation(value = "启用禁用mcc")
    @PostMapping("banMcc")
    public BaseResponse banMcc(@RequestBody @ApiParam MccDTO mccDto) {
        mccDto.setModifier(this.getSysUserVO().getUsername());
        mccDto.setLanguage(this.getLanguage());
        return ResultUtil.success(mccService.banMcc(mccDto));
    }

    @ApiOperation(value = "查询所有mcc")
    @GetMapping("inquireAllMcc")
    public BaseResponse inquireAllMcc() {
        return ResultUtil.success(mccService.inquireAllMcc(this.getLanguage()));
    }


    @ApiOperation(value = "导入mcc")
    @PostMapping("importMcc")
    public BaseResponse importMcc(@RequestBody @ApiParam List<Mcc> list) {
        return ResultUtil.success(mccService.importMcc(list));
    }

    @ApiOperation(value = "导出mcc")
    @PostMapping("exportMcc")
    public BaseResponse exportMcc(@RequestBody @ApiParam MccDTO mccDto) {
        mccDto.setLanguage(this.getLanguage());
        return ResultUtil.success(mccService.exportMcc(mccDto));
    }
}
