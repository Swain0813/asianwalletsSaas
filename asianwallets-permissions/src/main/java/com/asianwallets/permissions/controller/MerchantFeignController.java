package com.asianwallets.permissions.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.base.MerchantFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-27 10:03
 **/
@RestController
@Api(description = "商户管理接口")
@RequestMapping("/merchant")
public class MerchantFeignController extends BaseController {

    @Autowired
    private MerchantFeign merchantFeign;

    @ApiOperation(value = "添加商户")
    @PostMapping("addMerchant")
    public BaseResponse addMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        return merchantFeign.addMerchant(merchantDTO);
    }

    @ApiOperation(value = "修改商户")
    @PostMapping("updateMerchant")
    public BaseResponse updateMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        return merchantFeign.updateMerchant(merchantDTO);
    }

    @ApiOperation(value = "分页查询商户信息列表")
    @PostMapping("/pageFindMerchant")
    public BaseResponse pageFindMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        return merchantFeign.pageFindMerchant(merchantDTO);
    }

    @ApiOperation(value = "分页查询商户审核信息列表")
    @PostMapping("/pageFindMerchantAudit")
    public BaseResponse pageFindMerchantAudit(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        return merchantFeign.pageFindMerchantAudit(merchantDTO);
    }

    @ApiOperation(value = "根据商户Id查询商户信息详情")
    @GetMapping("/getMerchantInfo")
    public BaseResponse getMerchantInfo(@RequestParam @ApiParam String id) {
        return merchantFeign.getMerchantInfo(id);
    }

    @ApiOperation(value = "根据商户Id查询商户审核信息详情")
    @GetMapping("/getMerchantAuditInfo")
    public BaseResponse getMerchantAuditInfo(@RequestParam @ApiParam String id) {
        return merchantFeign.getMerchantAuditInfo(id);
    }

    @ApiOperation(value = "审核商户信息接口")
    @GetMapping("/auditMerchant")
    public BaseResponse auditMerchant(@RequestParam @ApiParam String merchantId, @RequestParam @ApiParam Boolean enabled, @RequestParam(required = false) @ApiParam String remark) {
        return merchantFeign.auditMerchant(merchantId, enabled, remark);
    }
}
