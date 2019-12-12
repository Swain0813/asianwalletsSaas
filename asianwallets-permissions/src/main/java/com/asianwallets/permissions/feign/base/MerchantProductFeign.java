package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.MerchantProduct;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.MerChannelVO;
import com.asianwallets.permissions.feign.base.impl.MerchantProductFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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

    @ApiOperation(value = "根据商户通道Id查询商户通道详情")
    @GetMapping("/merchantProduct/getMerChannelInfoById")
    BaseResponse getMerChannelInfoById(@RequestParam("merChannelId") @ApiParam String merChannelId);

    @ApiOperation(value = "修改机构通道")
    @PostMapping("/merchantProduct/updateMerchantChannel")
    BaseResponse updateMerchantChannel(@RequestBody @ApiParam BatchUpdateSortDTO batchUpdateSort);

    @ApiOperation(value = "查询商户分配通道关联关系")
    @GetMapping("/merchantProduct/getRelevantInfo")
    BaseResponse getRelevantInfo(@RequestParam("merchantId") @ApiParam String merchantId);


    @ApiOperation(value = "导出商户产品信息")
    @PostMapping("/merchantProduct/exportMerProduct")
    List<MerchantProduct> exportMerProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO);

    @ApiOperation(value = "导出商户通道信息")
    @PostMapping("/merchantProduct/exportMerChannel")
    List<MerChannelVO> exportMerChannel(@RequestBody @ApiParam SearchChannelDTO searchChannelDTO);
}
