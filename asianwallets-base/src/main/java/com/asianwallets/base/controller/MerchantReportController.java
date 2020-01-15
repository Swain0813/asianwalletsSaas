package com.asianwallets.base.controller;

import com.asianwallets.base.service.MerchantReportService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.MerchantReportDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.MerchantReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName mcc
 * @Description 商户报备
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/merchantReport")
@Api(description = "商户报备接口")
public class MerchantReportController extends BaseController {

    @Autowired
    private MerchantReportService merchantReportService;

    @ApiOperation(value = "新增报备信息")
    @PostMapping("addReport")
    public BaseResponse addReport(@RequestBody @ApiParam MerchantReportDTO merchantReportDTO) {
        merchantReportDTO.setCreator(this.getUserName().getUsername());
        return ResultUtil.success(merchantReportService.addReport(merchantReportDTO));
    }

    @ApiOperation(value = "查询报备信息")
    @PostMapping("pageReport")
    public BaseResponse pageReport(@RequestBody @ApiParam MerchantReportDTO merchantReportDTO) {
        return ResultUtil.success(merchantReportService.pageReport(merchantReportDTO));
    }

    @ApiOperation(value = "修改报备信息")
    @PostMapping("updateReport")
    public BaseResponse updateReport(@RequestBody @ApiParam MerchantReportDTO merchantReportDTO) {
        merchantReportDTO.setModifier(this.getUserName().getUsername());
        return ResultUtil.success(merchantReportService.updateReport(merchantReportDTO));
    }

    @ApiOperation(value = "启用禁用Report")
    @PostMapping("banReport")
    public BaseResponse banReport(@RequestBody @ApiParam MerchantReportDTO merchantReportDTO) {
        merchantReportDTO.setModifier(this.getUserName().getUsername());
        return ResultUtil.success(merchantReportService.banReport(merchantReportDTO));
    }

    @ApiOperation(value = "导出Report")
    @PostMapping("exportReport")
    public List<MerchantReportVO> exportReport(@RequestBody @ApiParam MerchantReportDTO merchantReportDTO) {
        return merchantReportService.exportReport(merchantReportDTO);
    }
}
