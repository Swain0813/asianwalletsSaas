package com.asianwallets.base.controller;

import com.asianwallets.base.service.MerchantProductService;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.MerchantProduct;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.asianwallets.common.base.BaseController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yx
 * @since 2019-12-09
 */
@Api(description = "商户产品管理")
@RestController
@RequestMapping("/merchantProduct")
public class MerchantProductController extends BaseController {

    @Autowired
    private MerchantProductService merchantProductService;

    @ApiOperation(value = "添加商户产品")
    @PostMapping("/addMerchantProduct")
    public BaseResponse addMerchantProduct(@RequestBody @ApiParam List<MerchantProductDTO> merchantProductDTOs) {
        return ResultUtil.success(merchantProductService.addMerchantProduct(this.getSysUserVO().getName(), merchantProductDTOs));
    }

    @ApiOperation(value = "修改商户产品")
    @PostMapping("/updateMerchantProduct")
    public BaseResponse updateMerchantProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        return ResultUtil.success(merchantProductService.updateMerchantProduct(this.getSysUserVO().getName(), merchantProductDTO));
    }

    @ApiOperation(value = "批量审核商户产品")
    @PostMapping("/auditMerchantProduct")
    public BaseResponse auditMerchantProduct(@RequestBody @ApiParam AuaditProductDTO auaditProductDTO) {
        BaseResponse baseResponse = merchantProductService.auditMerchantProduct(this.getSysUserVO().getUsername(), auaditProductDTO);
        String code = baseResponse.getCode();//业务返回码
        if (org.springframework.util.StringUtils.isEmpty(code)) {
            baseResponse.setCode(EResultEnum.SUCCESS.getCode());
            baseResponse.setMsg("SUCCESS");
            return baseResponse;
        }
        return ResultUtil.error(code, this.getErrorMsgMap(code));
    }

    @ApiOperation(value = "商户分配通道")
    @PostMapping("/allotMerProductChannel")
    public BaseResponse allotMerProductChannel(@RequestBody @ApiParam @Valid MerProDTO merProDTO) {
        return ResultUtil.success(merchantProductService.allotMerProductChannel(this.getSysUserVO().getUsername(), merProDTO));
    }

    @ApiOperation(value = "分页查询商户产品信息")
    @PostMapping("/pageFindMerProduct")
    public BaseResponse pageFindMerProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        if (StringUtils.isBlank(merchantProductDTO.getLanguage())) {
            merchantProductDTO.setLanguage(this.getLanguage());
        }
        return ResultUtil.success(merchantProductService.pageFindMerProduct(merchantProductDTO));
    }

    @ApiOperation(value = "根据产品Id查询商户产品详情")
    @GetMapping("/getMerProductById")
    public BaseResponse getMerProductById(@RequestParam @ApiParam String merProductId) {
        return ResultUtil.success(merchantProductService.getMerProductById(merProductId));
    }

    @ApiOperation(value = "分页查询商户审核产品信息")
    @PostMapping("/pageFindMerProductAudit")
    public BaseResponse pageFindMerProductAudit(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        if (StringUtils.isBlank(merchantProductDTO.getLanguage())) {
            merchantProductDTO.setLanguage(this.getLanguage());
        }
        return ResultUtil.success(merchantProductService.pageFindMerProductAudit(merchantProductDTO));
    }

    @ApiOperation(value = "根据Id查询商户产品审核详情")
    @GetMapping("/getMerProductAuditById")
    public BaseResponse getMerProductAuditById(@RequestParam @ApiParam String merProductId) {
        return ResultUtil.success(merchantProductService.getMerProductAuditById(merProductId));
    }

    @ApiOperation(value = "分页查询商户产品通道管理信息")
    @PostMapping("/pageFindMerProChannel")
    public BaseResponse pageFindMerProChannel(@RequestBody @ApiParam SearchChannelDTO searchChannelDTO) {
        if (StringUtils.isBlank(searchChannelDTO.getLanguage())) {
            searchChannelDTO.setLanguage(this.getLanguage());
        }
        return ResultUtil.success(merchantProductService.pageFindMerProChannel(searchChannelDTO));
    }

    @ApiOperation(value = "根据商户通道Id查询商户通道详情")
    @GetMapping("/getMerChannelInfoById")
    public BaseResponse getMerChannelInfoById(@RequestParam @ApiParam String merChannelId) {
        return ResultUtil.success(merchantProductService.getMerChannelInfoById(merChannelId));
    }

    @ApiOperation(value = "修改机构通道")
    @PostMapping("/updateMerchantChannel")
    public BaseResponse updateMerchantChannel(@RequestBody @ApiParam BatchUpdateSortDTO batchUpdateSort) {
        return ResultUtil.success(merchantProductService.updateMerchantChannel(this.getSysUserVO().getUsername(), batchUpdateSort));
    }

    @ApiOperation(value = "查询商户分配通道关联关系")
    @GetMapping("/getRelevantInfo")
    public BaseResponse getRelevantInfo(@RequestParam @ApiParam String merchantId) {
        return ResultUtil.success(merchantProductService.getRelevantInfo(merchantId));
    }


    @ApiOperation(value = "导出商户产品信息")
    @PostMapping("/exportMerProduct")
    public List<MerchantProduct> exportMerProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        if (StringUtils.isBlank(merchantProductDTO.getLanguage())) {
            merchantProductDTO.setLanguage(this.getLanguage());
        }
        return merchantProductService.exportMerProduct(merchantProductDTO);
    }

}
