package com.asianwallets.permissions.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.ReconOperDTO;
import com.asianwallets.common.dto.ReconciliationDTO;
import com.asianwallets.common.dto.SearchAvaBalDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.permissions.feign.base.ReconciliationFeign;
import com.asianwallets.permissions.service.OperationLogService;
import com.asianwallets.permissions.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@Api(description = "调账管理接口")
@RequestMapping("/reconciliation")
public class ReconciliationFeignController extends BaseController {

    @Autowired
    private ReconciliationFeign reconciliationFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private SysUserService sysUserService;


    @ApiOperation(value = "分页查询调账单")
    @PostMapping("/pageReconciliation")
    public BaseResponse pageReconciliation(@RequestBody @ApiParam ReconciliationDTO reconciliationDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(reconciliationDTO),
                "分页查询调账单"));
        return reconciliationFeign.pageReconciliation(reconciliationDTO);
    }

    @ApiOperation(value = "分页查询审核调账单")
    @PostMapping("/pageReviewReconciliation")
    public BaseResponse pageReviewReconciliation(@RequestBody @ApiParam ReconciliationDTO reconciliationDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(reconciliationDTO),
                "分页查询审核调账单"));
        return reconciliationFeign.pageReviewReconciliation(reconciliationDTO);
    }

    @ApiOperation(value = "资金变动操作")
    @PostMapping("/doReconciliation")
    public BaseResponse doReconciliation(@RequestBody @ApiParam @Valid ReconOperDTO reconOperDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(reconOperDTO),
                "资金变动操作"));
        return reconciliationFeign.doReconciliation(reconOperDTO);
    }

    @ApiOperation(value = "资金变动审核")
    @GetMapping("/auditReconciliation")
    public BaseResponse auditReconciliation(@RequestParam @ApiParam String reconciliationId, @RequestParam @ApiParam boolean enabled,
                                            @RequestParam(required = false) @ApiParam String remark, @RequestParam @ApiParam String tradePwd) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSONObject.toJSONString(this.getRequest().getParameterMap()),
                "资金变动审核"));
        SysUserVO sysUserVO = this.getSysUserVO();
        if (!sysUserService.checkPassword(sysUserService.decryptPassword(tradePwd), sysUserVO.getTradePassword())) {
            throw new BusinessException(EResultEnum.TRADE_PASSWORD_ERROR.getCode());
        }
        return reconciliationFeign.auditReconciliation(reconciliationId, enabled, remark);
    }

    @ApiOperation(value = "查询可用余额")
    @PostMapping("/getAvailableBalance")
    public BaseResponse getAvailableBalance(@RequestBody @ApiParam @Valid SearchAvaBalDTO searchAvaBalDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(searchAvaBalDTO),
                "查询可用余额"));
        return reconciliationFeign.getAvailableBalance(searchAvaBalDTO);
    }
}
