package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.SettleOrder;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.permissions.feign.base.SettleOrderFeign;
import com.asianwallets.permissions.service.OperationLogService;
import com.asianwallets.permissions.service.SettleOrderFeignService;
import com.asianwallets.permissions.service.SysUserService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 结算交易相关模块
 */
@RestController
@Api(description = "结算交易接口")
@RequestMapping("/settleorder")
@Slf4j
public class SettleOrderFeignController extends BaseController {

    @Autowired
    private SettleOrderFeign settleOrderFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private SettleOrderFeignService settleOrderFeignService;

    @Autowired
    private SysUserService sysUserService;


    @ApiOperation(value = "结算交易分页查询一览")
    @PostMapping("/pageSettleOrder")
    public BaseResponse pageSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(settleOrderDTO),
                "结算交易分页查询一览"));
        return settleOrderFeign.pageSettleOrder(settleOrderDTO);
    }

    @ApiOperation(value = "结算交易分页查询详情")
    @PostMapping("/pageSettleOrderDetail")
    public BaseResponse pageSettleOrderDetail(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(settleOrderDTO),
                "结算交易分页查询详情"));
        return settleOrderFeign.pageSettleOrderDetail(settleOrderDTO);
    }

    /**
     * saas后台结算审核导出
     *
     * @param settleOrderDTO
     * @param response
     * @return
     */
    @ApiOperation(value = "saas后台结算审核导出")
    @PostMapping("/exportSettleOrder")
    public BaseResponse exportSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(settleOrderDTO),
                "saas后台结算审核导出"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<SettleOrder> lists = settleOrderFeign.exportSettleOrder(settleOrderDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(lists)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils excelUtils = new ExcelUtils();
            excelUtils.exportExcel(lists, SettleOrderExportDTO.class, writer);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【saas后台结算审核导出】==========【saas后台结算审核导出】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    /**
     * 其他系统结算导出功能
     *
     * @param settleOrderDTO
     * @return
     */
    @ApiOperation(value = "其他系统结算导出功能")
    @PostMapping("/exportOtherSettleOrder")
    public BaseResponse exportInsSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(settleOrderDTO),
                "其他系统结算导出功能"));
        ExcelWriter writer = null;
        try {
            List<SettleOrder> settleOrders = settleOrderFeign.exportSettleOrder(settleOrderDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(settleOrders)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ArrayList<SettleOrder> settleOrderArrayList = new ArrayList<>();
            for (SettleOrder settleOrder : settleOrders) {
                settleOrderArrayList.add(JSON.parseObject(JSON.toJSONString(settleOrder), SettleOrder.class));
            }
            if (AsianWalletConstant.EN_US.equals(this.getLanguage())) {//英文版的场合
                writer = settleOrderFeignService.getInsExcelWriter(settleOrderArrayList, SettleOrderInsEnExport.class);
            } else {
                writer = settleOrderFeignService.getInsExcelWriter(settleOrderArrayList, SettleOrderInsExport.class);
            }
            writer.flush(out);
        } catch (Exception e) {
            log.info("-----------------其他系统结算导出功能文件导出异常-----------------exception:{}", JSON.toJSONString(e));
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return ResultUtil.success();
    }


    @ApiOperation(value = "结算审核")
    @PostMapping("/reviewSettlement")
    public BaseResponse reviewSettlement(@RequestBody @ApiParam ReviewSettleDTO reviewSettleDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(reviewSettleDTO),
                "结算审核"));
        //校验交易密码
        if (!sysUserService.checkPassword(sysUserService.decryptPassword(reviewSettleDTO.getTradePwd()), this.getSysUserVO().getTradePassword())) {
            throw new BusinessException(EResultEnum.TRADE_PASSWORD_ERROR.getCode());
        }
        return settleOrderFeign.reviewSettlement(reviewSettleDTO);
    }

    @ApiOperation(value = "手动提款")
    @PostMapping("/withdrawal")
    public BaseResponse withdrawal(@RequestBody @ApiParam WithdrawalDTO withdrawalDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(withdrawalDTO),
                "手动提款"));
        //校验交易密码
        if (!sysUserService.checkPassword(withdrawalDTO.getTradePwd(), this.getSysUserVO().getTradePassword())) {
            throw new BusinessException(EResultEnum.TRADE_PASSWORD_ERROR.getCode());
        }
        return settleOrderFeign.withdrawal(withdrawalDTO);
    }

    @ApiOperation(value = "提款设置")
    @PostMapping("/updateAccountSettle")
    public BaseResponse updateAccountSettle(@RequestBody @ApiParam AccountSettleDTO accountSettleDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(accountSettleDTO),
                "提款设置"));
        return settleOrderFeign.updateAccountSettle(accountSettleDTO);
    }
}
