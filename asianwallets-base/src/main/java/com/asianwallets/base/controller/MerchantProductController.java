package com.asianwallets.base.controller;

import com.asianwallets.base.service.MerchantProductService;
import com.asianwallets.common.dto.AuaditProductDTO;
import com.asianwallets.common.dto.MerProDTO;
import com.asianwallets.common.dto.MerchantProductDTO;
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

}
