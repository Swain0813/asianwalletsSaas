package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.ReconOperDTO;
import com.asianwallets.common.dto.ReconciliationDTO;
import com.asianwallets.common.dto.SearchAvaBalDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.ReconciliationFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;

/**
 * 调账管理模块
 */
@FeignClient(value = "asianwallets-base", fallback = ReconciliationFeignImpl.class)
public interface ReconciliationFeign {

    @ApiOperation(value = "分页查询调账单")
    @PostMapping("/reconciliation/pageReconciliation")
    BaseResponse pageReconciliation(@RequestBody @ApiParam ReconciliationDTO reconciliationDTO);

    @ApiOperation(value = "分页查询审核调账单")
    @PostMapping("/reconciliation/pageReviewReconciliation")
    BaseResponse pageReviewReconciliation(@RequestBody @ApiParam ReconciliationDTO reconciliationDTO);

    @ApiOperation(value = "资金变动操作")
    @PostMapping("/reconciliation/doReconciliation")
    BaseResponse doReconciliation(@RequestBody @ApiParam ReconOperDTO reconOperDTO);

    @ApiOperation(value = "资金变动审核")
    @GetMapping("/reconciliation/auditReconciliation")
    BaseResponse auditReconciliation(@RequestParam("reconciliationId") @ApiParam String reconciliationId,
                                     @RequestParam("enabled") @ApiParam boolean enabled, @RequestParam("remark") @ApiParam String remark);

    @ApiOperation(value = "查询可用余额")
    @PostMapping("/reconciliation/getAvailableBalance")
    BaseResponse getAvailableBalance(@RequestBody @ApiParam @Valid SearchAvaBalDTO searchAvaBalDTO);
}
