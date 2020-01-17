package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.vo.ExportTradeAccountVO;
import com.asianwallets.permissions.feign.base.TradeCheckAccountFeign;
import com.asianwallets.permissions.service.ExportService;
import com.asianwallets.permissions.service.OperationLogService;
import com.asianwallets.permissions.vo.ExportTradeCheckAccountDetailEnVO;
import com.asianwallets.permissions.vo.ExportTradeCheckAccountDetailVO;
import com.asianwallets.permissions.vo.ExportTradeCheckAccountEnVO;
import com.asianwallets.permissions.vo.ExportTradeCheckAccountVO;
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
import javax.validation.Valid;

@Api(description = "商户交易对账单")
@RestController
@RequestMapping("/base")
@Slf4j
public class TradeCheckAccountFeignController extends BaseController {

    @Autowired
    private TradeCheckAccountFeign tradeCheckAccountFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ExportService exportService;

    @ApiOperation(value = "分页查询交易对账总表信息")
    @PostMapping("/pageFindTradeCheckAccount")
    public BaseResponse pageFindTradeCheckAccount(@RequestBody @ApiParam @Valid TradeCheckAccountDTO tradeCheckAccountDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(tradeCheckAccountDTO),
                "分页查询交易对账总表信息"));
        return tradeCheckAccountFeign.pageFindTradeCheckAccount(tradeCheckAccountDTO);
    }

    @ApiOperation(value = "分页查询交易对账详细表信息")
    @PostMapping("/pageFindTradeCheckAccountDetail")
    public BaseResponse pageFindTradeCheckAccountDetail(@RequestBody @ApiParam @Valid TradeCheckAccountDTO tradeCheckAccountDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(tradeCheckAccountDTO),
                "分页查询交易对账详细表信息"));
        return tradeCheckAccountFeign.pageFindTradeCheckAccountDetail(tradeCheckAccountDTO);
    }


    @ApiOperation(value = "导出商户交易对账单")
    @PostMapping("exportTradeCheckAccount")
    public BaseResponse exportTradeCheckAccount(@RequestBody @ApiParam @Valid TradeCheckAccountDTO tradeCheckAccountDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(tradeCheckAccountDTO),
                "导出商户交易对账单"));
        ExportTradeAccountVO exportTradeAccountVO = tradeCheckAccountFeign.exportTradeCheckAccount(tradeCheckAccountDTO);
        //数据不存在的场合
        if (exportTradeAccountVO == null || ArrayUtil.isEmpty(exportTradeAccountVO.getTradeCheckAccounts()) || ArrayUtil.isEmpty(exportTradeAccountVO.getTradeAccountDetailVOS())) {
            throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
        }
        ExcelWriter writer = null;
        try {
            if (AsianWalletConstant.EN_US.equals(this.getLanguage())) {
                //英文的场合
                writer = exportService.exportTradeCheckAccount(exportTradeAccountVO, this.getLanguage(), ExportTradeCheckAccountEnVO.class, ExportTradeCheckAccountDetailEnVO.class);
            } else {
                //中文的场合
                writer = exportService.exportTradeCheckAccount(exportTradeAccountVO, this.getLanguage(), ExportTradeCheckAccountVO.class, ExportTradeCheckAccountDetailVO.class);
            }
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            log.info("==============【导出商户交易对账单】==============【接口异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }
}
