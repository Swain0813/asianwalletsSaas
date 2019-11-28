package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.base.MerchantFeign;
import com.asianwallets.permissions.service.OperationLogService;
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

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "添加商户")
    @PostMapping("addMerchant")
    public BaseResponse addMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(merchantDTO),
                "添加商户"));
        return merchantFeign.addMerchant(merchantDTO);
    }

    @ApiOperation(value = "修改商户")
    @PostMapping("updateMerchant")
    public BaseResponse updateMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(merchantDTO),
                "修改商户"));
        return merchantFeign.updateMerchant(merchantDTO);
    }

    @ApiOperation(value = "分页查询商户信息列表")
    @PostMapping("/pageFindMerchant")
    public BaseResponse pageFindMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantDTO),
                "分页查询商户信息列表"));
        return merchantFeign.pageFindMerchant(merchantDTO);
    }

    @ApiOperation(value = "分页查询商户审核信息列表")
    @PostMapping("/pageFindMerchantAudit")
    public BaseResponse pageFindMerchantAudit(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantDTO),
                "分页查询商户审核信息列表"));
        return merchantFeign.pageFindMerchantAudit(merchantDTO);
    }

    @ApiOperation(value = "根据商户Id查询商户信息详情")
    @GetMapping("/getMerchantInfo")
    public BaseResponse getMerchantInfo(@RequestParam @ApiParam String id) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(id),
                "根据商户Id查询商户信息详情"));
        return merchantFeign.getMerchantInfo(id);
    }

    @ApiOperation(value = "根据商户Id查询商户审核信息详情")
    @GetMapping("/getMerchantAuditInfo")
    public BaseResponse getMerchantAuditInfo(@RequestParam @ApiParam String id) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(id),
                "根据商户Id查询商户审核信息详情"));
        return merchantFeign.getMerchantAuditInfo(id);
    }

    @ApiOperation(value = "审核商户信息接口")
    @GetMapping("/auditMerchant")
    public BaseResponse auditMerchant(@RequestParam @ApiParam String merchantId, @RequestParam @ApiParam Boolean enabled, @RequestParam(required = false) @ApiParam String remark) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(this.getRequest().getParameterMap()),
                "审核商户信息接口"));
        return merchantFeign.auditMerchant(merchantId, enabled, remark);
    }
}
