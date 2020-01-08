package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.dto.ClearSearchDTO;
import com.asianwallets.common.dto.FrozenMarginInfoDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.*;
import com.asianwallets.permissions.feign.base.AccountFeign;
import com.asianwallets.permissions.service.ExportService;
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

/**
 * 账户信息
 */
@RestController
@Api(description = "账户详情管理接口")
@RequestMapping("/account")
@Slf4j
public class AccountFeignController extends BaseController {

    @Autowired
    private AccountFeign accountFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ExportService exportService;


    @ApiOperation(value = "分页查询账户信息")
    @PostMapping("/pageFindAccount")
    public BaseResponse pageFindAccount(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(accountSearchDTO),
                "分页查询账户信息"));
        return accountFeign.pageFindAccount(accountSearchDTO);
    }

    @ApiOperation(value = "导出账户信息")
    @PostMapping("/exportAccountList")
    public BaseResponse exportAccountList(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(accountSearchDTO),
                "导出账户信息"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<AccountListVO> dataList = accountFeign.exportAccountList(accountSearchDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(dataList)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils excelUtils = new ExcelUtils();
            excelUtils.exportExcel(dataList, AccountListVO.class, writer);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出账户信息】==========【导出账户信息导出异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "查询清算户余额流水详情")
    @PostMapping("/pageClearLogs")
    public BaseResponse pageClearLogs(@RequestBody @ApiParam ClearSearchDTO clearSearchDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(clearSearchDTO),
                "查询清算户余额流水详情"));
        return accountFeign.pageClearLogs(clearSearchDTO);
    }

    @ApiOperation(value = "导出清算户余额流水详情")
    @PostMapping("/exportClearLogs")
    public BaseResponse exportClearLogs(@RequestBody @ApiParam ClearSearchDTO clearSearchDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(clearSearchDTO),
                "导出清算户余额流水详情"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<ClearAccountVO> dataList = accountFeign.exportClearLogs(clearSearchDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(dataList)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            writer = exportService.getClearBalanceWriter(dataList, ClearAccountVO.class);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出清算户余额流水详情】==========【导出清算户余额流水详情异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "查询结算户余额流水详情")
    @PostMapping("/pageSettleLogs")
    public BaseResponse pageSettleLogs(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(accountSearchDTO),
                "查询结算户余额流水详情"));
        return accountFeign.pageSettleLogs(accountSearchDTO);
    }

    @ApiOperation(value = "导出结算户余额流水详情")
    @PostMapping("/exportSettleLogs")
    public BaseResponse exportSettleLogs(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(accountSearchDTO),
                "导出结算户余额流水详情"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<TmMerChTvAcctBalanceVO> dataList = accountFeign.exportSettleLogs(accountSearchDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(dataList)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            writer = exportService.getTmMerChTvAcctBalanceWriter(dataList, TmMerChTvAcctBalanceVO.class);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出结算户余额流水详情】==========【导出结算户余额流水详情异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "查询冻结余额流水详情")
    @PostMapping("/pageFrozenLogs")
    public BaseResponse pageFrozenLogs(@RequestBody @ApiParam FrozenMarginInfoDTO accountSearchDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(accountSearchDTO),
                "查询冻结余额流水详情"));
        return accountFeign.pageFrozenLogs(accountSearchDTO);
    }

    @ApiOperation(value = "导出冻结余额流水详情")
    @PostMapping("/exportFrozenLogs")
    public BaseResponse exportFrozenLogs(@RequestBody @ApiParam FrozenMarginInfoDTO frozenMarginInfoDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(frozenMarginInfoDTO),
                "导出冻结余额流水详情"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<FrozenMarginInfoVO> dataList = accountFeign.exportFrozenLogs(frozenMarginInfoDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(dataList)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            writer = exportService.getFrozenLogsWriter(dataList, FrozenMarginInfoVO.class);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出冻结余额流水详情】==========【导出冻结余额流水详情异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "分页查询商户余额")
    @PostMapping("/pageFindMerchantBalance")
    public BaseResponse pageMerchantBalance(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(ordersDTO),
                "分页查询商户余额"));
        return accountFeign.pageFindMerchantBalance(ordersDTO);
    }

    @ApiOperation(value = "导出商户余额")
    @PostMapping("/exportMerchantBalance")
    public BaseResponse exportMerchantBalance(@RequestBody @ApiParam OrdersDTO ordersDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(ordersDTO),
                "导出商户余额"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            final List<MerchantBalanceVO> merchantBalanceVOList = accountFeign.exportMerchantBalance(ordersDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(merchantBalanceVOList)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils excelUtils = new ExcelUtils();
            excelUtils.exportExcel(merchantBalanceVOList, MerchantBalanceVO.class, writer);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出商户余额】==========【导出商户余额异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }
}
