package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.BankIssuerIdDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.BankIssuerIdVO;
import com.asianwallets.permissions.feign.base.BankIssuerIdFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(description = "银行机构代码映射接口")
@RequestMapping("/bankIssuerId")
@Slf4j
public class BankIssuerIdFeignController extends BaseController {

    @Autowired
    private BankIssuerIdFeign bankIssuerIdFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "添加银行机构代码映射信息")
    @PostMapping("/addBankIssuerId")
    public BaseResponse addBankIssuerId(@RequestBody @ApiParam List<BankIssuerIdDTO> bankIssuerIdDTOList) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(bankIssuerIdDTOList),
                "添加银行机构代码映射信息"));
        return bankIssuerIdFeign.addBankIssuerId(bankIssuerIdDTOList);
    }

    @ApiOperation(value = "修改银行机构代码映射")
    @PostMapping("/updateBankIssuerId")
    public BaseResponse updateBankIssuerId(@RequestBody @ApiParam BankIssuerIdDTO bankIssuerIdDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(bankIssuerIdDTO),
                "修改银行机构代码映射"));
        return bankIssuerIdFeign.updateBankIssuerId(bankIssuerIdDTO);
    }

    @ApiOperation(value = "查询银行机构代码映射信息")
    @PostMapping("/pageFindBankIssuerId")
    public BaseResponse pageFindBankIssuerId(@RequestBody @ApiParam BankIssuerIdDTO bankIssuerIdDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(bankIssuerIdDTO),
                "查询银行机构代码映射信息"));
        return bankIssuerIdFeign.pageFindBankIssuerId(bankIssuerIdDTO);
    }

    @ApiOperation(value = "导出银行机构代码映射信息")
    @PostMapping("/exportBankIssuerId")
    public BaseResponse exportBankIssuerId(@RequestBody @ApiParam BankIssuerIdDTO bankIssuerIdDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(bankIssuerIdDTO),
                "导出银行机构代码映射信息"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<BankIssuerIdVO> dataList = bankIssuerIdFeign.exportBankIssuerId(bankIssuerIdDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(dataList)) {
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils excelUtils = new ExcelUtils();
            excelUtils.exportExcel(dataList, BankIssuerIdVO.class, writer);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出银行机构代码映射信息】==========【导出银行机构代码映射信息异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }
}
