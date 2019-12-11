package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.ExportBankVO;
import com.asianwallets.permissions.feign.base.BankFeign;
import com.asianwallets.permissions.service.ImportService;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(description = "银行接口")
@RequestMapping("/bank")
@Slf4j
public class BankFeignController extends BaseController {

    @Autowired
    private BankFeign bankFeign;

    @Autowired
    private ImportService importService;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "增加银行")
    @PostMapping("/addBank")
    public BaseResponse addBank(@RequestBody @ApiParam BankDTO bankDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(bankDTO),
                "增加银行"));
        return bankFeign.addBank(bankDTO);
    }

    @ApiOperation(value = "修改银行信息")
    @PostMapping("/updateBank")
    public BaseResponse updateBank(@RequestBody @ApiParam BankDTO bankDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(bankDTO),
                "修改银行信息"));
        return bankFeign.updateBank(bankDTO);
    }

    @ApiOperation(value = "分页查询银行信息")
    @PostMapping("/pageFindBank")
    public BaseResponse pageFindBank(@RequestBody @ApiParam BankDTO bankDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(bankDTO),
                "分页查询银行信息"));
        return bankFeign.pageFindBank(bankDTO);
    }

    @ApiOperation(value = "导入银行信息")
    @PostMapping("/importBank")
    public BaseResponse importBank(@RequestParam("file") @ApiParam MultipartFile file) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(file),
                "导入银行信息"));
        return bankFeign.importBank(importService.importBank(getSysUserVO().getUsername(), file));
    }

    @ApiOperation(value = "导出银行信息")
    @PostMapping("/exportBank")
    public BaseResponse exportBank(@RequestBody @ApiParam BankDTO bankDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(bankDTO),
                "导出银行信息"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<ExportBankVO> dataList = bankFeign.exportBank(bankDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(dataList)) {
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils excelUtils = new ExcelUtils();
            excelUtils.exportExcel(dataList, ExportBankVO.class, writer);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出银行信息】==========【导出银行信息异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }
}
