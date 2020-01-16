package com.asianwallets.permissions.controller;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.dto.TradeCheckAccountSettleExportDTO;
import com.asianwallets.common.entity.SettleCheckAccount;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.ExportSettleCheckAccountDetailEnVO;
import com.asianwallets.common.vo.ExportSettleCheckAccountDetailVO;
import com.asianwallets.common.vo.SettleCheckAccountEnVO;
import com.asianwallets.permissions.feign.base.SettleCheckAccountFeign;
import com.asianwallets.permissions.service.ExportService;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 结算对账单
 */
@RestController
@Api(description = "结算对账单")
@RequestMapping("/settleCheckAccount")
public class SettleCheckAccountFeignController extends BaseController {

    @Autowired
    private SettleCheckAccountFeign settleCheckAccountFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ExportService exportService;


    @ApiOperation(value = "查询前一天所有结算记录")
    @GetMapping("selectTcsStFlow")
    public BaseResponse selectTcsStFlow(@RequestParam @ApiParam String time) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(time),
                "查询前一天所有结算记录"));
        return settleCheckAccountFeign.selectTcsStFlow(time);
    }

    @ApiOperation(value = "分页查询商户结算对账")
    @PostMapping("pageSettleAccountCheck")
    public BaseResponse pageSettleAccountCheck(@RequestBody @ApiParam TradeCheckAccountDTO tradeCheckAccountDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(tradeCheckAccountDTO),
                "分页查询商户结算对账"));
        return settleCheckAccountFeign.pageSettleAccountCheck(tradeCheckAccountDTO);
    }

    @ApiOperation(value = "分页查询商户结算对账详情")
    @PostMapping("pageSettleAccountCheckDetail")
    public BaseResponse pageSettleAccountCheckDetail(@RequestBody @ApiParam TradeCheckAccountDTO tradeCheckAccountDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(tradeCheckAccountDTO),
                "分页查询商户结算对账详情"));
        return settleCheckAccountFeign.pageSettleAccountCheckDetail(tradeCheckAccountDTO);
    }

    @ApiOperation(value = "导出商户结算对账单")
    @PostMapping("exportSettleAccountCheck")
    public BaseResponse exportSettleAccountCheck(@RequestBody @ApiParam TradeCheckAccountSettleExportDTO tradeCheckAccountDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(tradeCheckAccountDTO),
                "导出商户结算对账单"));
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) settleCheckAccountFeign.exportSettleAccountCheck(tradeCheckAccountDTO);
        if (map == null || map.size() == 0) {//数据不存在的场合
            throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
        }
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            for (String key : map.keySet()) {
                if (key.equals("Statement")) {
                    writer.renameSheet(key);
                    List<SettleCheckAccount> settleCheckAccounts = JSON.parseArray(JSON.toJSONString(map.get(key)), SettleCheckAccount.class);
                    if(AsianWalletConstant.EN_US.equals(this.getLanguage())){
                        //英文版的场合
                        writer = exportService.getSettleCheckAccountsWriter(writer,AsianWalletConstant.EN_US, settleCheckAccounts, SettleCheckAccountEnVO.class);
                    }else {
                        writer = exportService.getSettleCheckAccountsWriter(writer,  AsianWalletConstant.ZH_CN,settleCheckAccounts, SettleCheckAccount.class);
                    }
                } else {
                    writer.setSheet(key);
                    List<ExportSettleCheckAccountDetailVO> settleCheckAccountDetails = JSON.parseArray(JSON.toJSONString(map.get(key)), ExportSettleCheckAccountDetailVO.class);
                    if(AsianWalletConstant.EN_US.equals(this.getLanguage())) {
                        //英文版的场合
                        writer = exportService.getSettleCheckAccountDetailWriter(writer, settleCheckAccountDetails, ExportSettleCheckAccountDetailEnVO.class);
                    }else {
                        writer = exportService.getSettleCheckAccountDetailWriter(writer,settleCheckAccountDetails, ExportSettleCheckAccountDetailVO.class);
                    }
                }
            }
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

}
