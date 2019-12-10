package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.*;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.MerchantProductFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "asianwallets-base", fallback = MerchantProductFeignImpl.class)
public interface MerchantProductFeign {

    @ApiOperation(value = "添加商户产品")
    @PostMapping("/merchantProduct/addMerchantProduct")
    BaseResponse addMerchantProduct(@RequestBody @ApiParam List<MerchantProductDTO> merchantProductDTOs);

    @ApiOperation(value = "修改商户产品")
    @PostMapping("/merchantProduct/updateMerchantProduct")
    BaseResponse updateMerchantProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO);

    @ApiOperation(value = "批量审核商户产品")
    @PostMapping("/merchantProduct/auditMerchantProduct")
    BaseResponse auditMerchantProduct(@RequestBody @ApiParam AuaditProductDTO auaditProductDTO);

    @ApiOperation(value = "商户分配通道")
    @PostMapping("/merchantProduct/allotMerProductChannel")
    BaseResponse allotMerProductChannel(@RequestBody @ApiParam @Valid MerProDTO merProDTO);

    @ApiOperation(value = "分页查询商户产品信息")
    @PostMapping("/merchantProduct/pageFindMerProduct")
    BaseResponse pageFindMerProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO);

    @ApiOperation(value = "根据产品Id查询商户产品详情")
    @GetMapping("/merchantProduct/getMerProductById")
    BaseResponse getMerProductById(@RequestParam("merProductId") @ApiParam String merProductId);

    @ApiOperation(value = "分页查询商户审核产品信息")
    @PostMapping("/merchantProduct/pageFindMerProductAudit")
    BaseResponse pageFindMerProductAudit(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO);

    @ApiOperation(value = "根据Id查询商户产品审核详情")
    @GetMapping("/merchantProduct/getMerProductAuditById")
    BaseResponse getMerProductAuditById(@RequestParam("merProductId") @ApiParam String merProductId);

    @ApiOperation(value = "分页查询商户产品通道管理信息")
    @PostMapping("/merchantProduct/pageFindMerProChannel")
    BaseResponse pageFindMerProChannel(@RequestBody @ApiParam SearchChannelDTO searchChannelDTO);

    @ApiOperation(value = "修改机构通道")
    @PostMapping("/merchantProduct/updateMerchantChannel")
    BaseResponse updateMerchantChannel(@RequestBody @ApiParam BatchUpdateSortDTO batchUpdateSort);

    @ApiOperation(value = "查询商户分配通道关联关系")
    @GetMapping("/merchantProduct/getRelevantInfo")
    BaseResponse getRelevantInfo(@RequestParam("merchantId") @ApiParam String merchantId);


}
