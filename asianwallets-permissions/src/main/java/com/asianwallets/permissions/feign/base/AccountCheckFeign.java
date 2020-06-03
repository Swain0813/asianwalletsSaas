package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.SearchAccountCheckDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.AccountCheckFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 对账管理
 */
@FeignClient(value = "asianwallets-base", fallback = AccountCheckFeignImpl.class)
public interface AccountCheckFeign {

    @ApiOperation(value = "分页查询对账管理")
    @PostMapping("/finance/pageAccountCheckLog")
    BaseResponse pageAccountCheckLog(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO);

    @ApiOperation(value = "导入通道对账单")
    @PostMapping("/finance/channelAccountCheck")
    BaseResponse channelAccountCheck(@RequestParam("file") @ApiParam MultipartFile file);

    @ApiOperation(value = "分页查询对账管理详情")
    @PostMapping("/finance/pageAccountCheck")
    BaseResponse pageAccountCheck(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO);

    /**
     * 差错处理一览 以及 对账管理详情用
     * @param searchAccountCheckDTO
     * @return
     */
    @ApiOperation(value = "导出对账管理详情")
    @PostMapping("/finance/exportAccountCheck")
    BaseResponse exportAccountCheck(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO);

    @ApiOperation(value = "差错处理和补单")
    @GetMapping("/finance/updateCheckAccount")
    BaseResponse updateCheckAccount(@RequestParam("checkAccountId")  @ApiParam String checkAccountId
            , @RequestParam("remark") @ApiParam String remark);

    /**
     * 差错复核一览
     * @param searchAccountCheckDTO
     * @return
     */
    @ApiOperation(value = "分页查询对账管理复核详情")
    @PostMapping("/finance/pageAccountCheckAudit")
    BaseResponse pageAccountCheckAudit(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO);

    /**
     * 差错复核导出
     * @param searchAccountCheckDTO
     * @return
     */
    @ApiOperation(value = "导出对账管理复核详情")
    @PostMapping("/finance/exportAccountCheckAudit")
    BaseResponse exportAccountCheckAudit(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO);

    @ApiOperation(value = "差错复核")
    @GetMapping("/finance/auditCheckAccount")
    BaseResponse auditCheckAccount(@RequestParam("checkAccountId")  @ApiParam String checkAccountId
            , @RequestParam("enable") @ApiParam Boolean enable, @RequestParam("remark") @ApiParam String remark);
}
