package com.asianwallets.base.controller;
import com.asianwallets.base.service.ReconciliationService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.ReconOperDTO;
import com.asianwallets.common.dto.ReconciliationDTO;
import com.asianwallets.common.dto.SearchAvaBalDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 调账管理模块
 */
@RestController
@Api(description = "调账管理")
@RequestMapping("/reconciliation")
public class ReconciliationController extends BaseController {

    @Autowired
    private ReconciliationService reconciliationService;


    @ApiOperation(value = "分页查询调账单")
    @PostMapping("pageReconciliation")
    public BaseResponse pageReconciliation(@RequestBody @ApiParam ReconciliationDTO reconciliationDTO) {
        return ResultUtil.success(reconciliationService.pageReconciliation(reconciliationDTO));
    }

    @ApiOperation(value = "分页查询审核调账单")
    @PostMapping("pageReviewReconciliation")
    public BaseResponse pageReviewReconciliation(@RequestBody @ApiParam ReconciliationDTO reconciliationDTO) {
        return ResultUtil.success(reconciliationService.pageReviewReconciliation(reconciliationDTO));
    }

    @ApiOperation(value = "资金变动操作")
    @PostMapping("doReconciliation")
    public BaseResponse doReconciliation(@RequestBody @ApiParam @Valid ReconOperDTO reconOperDTO) {
        return ResultUtil.success(reconciliationService.doReconciliation(this.getSysUserVO().getUsername(),reconOperDTO));
    }

    @ApiOperation(value = "资金变动审核")
    @GetMapping("auditReconciliation")
    public BaseResponse auditReconciliation(@RequestParam @ApiParam String reconciliationId, @RequestParam @ApiParam boolean enabled
            , @RequestParam(required = false) @ApiParam String  remark) {
        return ResultUtil.success(reconciliationService.auditReconciliation(this.getSysUserVO().getUsername(),reconciliationId,enabled,remark));
    }

    @ApiOperation(value = "查询可用余额")
    @PostMapping("/getAvailableBalance")
    public BaseResponse getAvailableBalance(@RequestBody @ApiParam @Valid SearchAvaBalDTO searchAvaBalDTO) {
        return ResultUtil.success(reconciliationService.getAvailableBalance(searchAvaBalDTO));
    }
}
