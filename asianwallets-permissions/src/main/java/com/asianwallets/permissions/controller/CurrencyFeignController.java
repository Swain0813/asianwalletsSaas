package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.CurrencyDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.CurrencyExportVO;
import com.asianwallets.permissions.feign.base.CurrencyFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName CurrencyController
 * @Description 币种
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/currency")
@Api(description = "币种管理")
@Slf4j
public class CurrencyFeignController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private CurrencyFeign currencyFeign;

    @ApiOperation(value = "新增币种")
    @PostMapping("addCurrency")
    public BaseResponse addCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(currencyDTO),
                "新增币种"));
        return ResultUtil.success(currencyFeign.addCurrency(currencyDTO));
    }

    @ApiOperation(value = "修改币种")
    @PostMapping("updateCurrency")
    public BaseResponse updateCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(currencyDTO),
                "修改币种"));
        return ResultUtil.success(currencyFeign.updateCurrency(currencyDTO));
    }

    @ApiOperation(value = "查询币种")
    @PostMapping("pageCurrency")
    public BaseResponse pageCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(currencyDTO),
                "查询币种"));
        return ResultUtil.success(currencyFeign.pageCurrency(currencyDTO));
    }

    @ApiOperation(value = "启用禁用币种")
    @PostMapping("banCurrency")
    public BaseResponse banDeviceVendor(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(currencyDTO),
                "启用禁用币种"));
        return ResultUtil.success(currencyFeign.banCurrency(currencyDTO));
    }

    @ApiOperation(value = "查询所有币种")
    @GetMapping("inquireAllCurrency")
    public BaseResponse inquireAllCurrency() {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, null,
                "查询所有币种"));
        return ResultUtil.success(currencyFeign.inquireAllCurrency());
    }

    @ApiOperation(value = "导出币种信息")
    @PostMapping("/exportCurrency")
    public BaseResponse exportCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(currencyDTO),
                "导出币种信息"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<CurrencyExportVO> dataList = currencyFeign.exportCurrency(currencyDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(dataList)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils<CurrencyExportVO> excelUtils = new ExcelUtils<>();
            excelUtils.exportExcel(dataList, CurrencyExportVO.class, writer);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出币种信息】==========【导出币种信息】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }
}
