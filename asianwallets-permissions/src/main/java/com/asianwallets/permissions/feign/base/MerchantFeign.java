package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.base.impl.MerchantFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "asianwallets-base", fallback = MerchantFeignImpl.class)
public interface MerchantFeign {

    @ApiOperation(value = "添加商户")
    @PostMapping("/merchant/addMerchant")
    BaseResponse addMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO);

    @ApiOperation(value = "修改商户")
    @PostMapping("/merchant/updateMerchant")
    BaseResponse updateMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO);

    @ApiOperation(value = "分页查询商户信息列表")
    @PostMapping("/merchant/pageFindMerchant")
    BaseResponse pageFindMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO);

    @ApiOperation(value = "分页查询商户审核信息列表")
    @PostMapping("/merchant/pageFindMerchantAudit")
    BaseResponse pageFindMerchantAudit(@RequestBody @ApiParam MerchantDTO merchantDTO);

    @ApiOperation(value = "根据商户Id查询商户信息详情")
    @GetMapping("/merchant/getMerchantInfo")
    BaseResponse getMerchantInfo(@RequestParam("id") @ApiParam String id);

    @ApiOperation(value = "根据商户Id查询商户审核信息详情")
    @GetMapping("/merchant/getMerchantAuditInfo")
    BaseResponse getMerchantAuditInfo(@RequestParam("id") @ApiParam String id);

    @ApiOperation(value = "审核商户信息接口")
    @GetMapping("/merchant/auditMerchant")
    BaseResponse auditMerchant(@RequestParam("merchantId") @ApiParam String merchantId, @RequestParam("enabled") @ApiParam Boolean enabled,
                               @RequestParam("remark") @ApiParam String remark);

    @ApiOperation(value = "代理商下拉框")
    @GetMapping("/merchant/getAllAgent")
    BaseResponse getAllAgent(@RequestParam("merchantType") @ApiParam String merchantType);

    @ApiOperation(value = "导出商户")
    @PostMapping("/merchant/exportMerchant")
    BaseResponse exportMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO);

    @ApiOperation(value = "禁用启用商户")
    @GetMapping("/merchant/banMerchant")
    BaseResponse banMerchant(@RequestParam @ApiParam String merchantId, @RequestParam @ApiParam Boolean enabled);

}
