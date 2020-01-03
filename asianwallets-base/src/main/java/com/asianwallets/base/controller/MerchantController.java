package com.asianwallets.base.controller;
import com.asianwallets.base.service.MerchantService;
import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.asianwallets.common.base.BaseController;
import java.util.List;

@Api(description = "商户管理")
@RestController
@RequestMapping("/merchant")
public class MerchantController extends BaseController {

    @Autowired
    private MerchantService merchantService;

    @ApiOperation(value = "添加商户")
    @PostMapping("addMerchant")
    public BaseResponse addMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        return ResultUtil.success(merchantService.addMerchant(this.getSysUserVO().getName(), merchantDTO));
    }

    @ApiOperation(value = "修改商户")
    @PostMapping("updateMerchant")
    public BaseResponse updateMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        return ResultUtil.success(merchantService.updateMerchant(this.getSysUserVO().getName(), merchantDTO));
    }

    @ApiOperation(value = "分页查询商户信息列表")
    @PostMapping("/pageFindMerchant")
    public BaseResponse pageFindMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        return ResultUtil.success(merchantService.pageFindMerchant(merchantDTO));
    }

    @ApiOperation(value = "导出商户")
    @PostMapping("/exportMerchant")
    public List<Merchant> exportMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO){
        return merchantService.exportMerchant(merchantDTO);
    }


    @ApiOperation(value = "分页查询商户审核信息列表")
    @PostMapping("/pageFindMerchantAudit")
    public BaseResponse pageFindMerchantAudit(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        return ResultUtil.success(merchantService.pageFindMerchantAudit(merchantDTO));
    }

    @ApiOperation(value = "根据商户Id查询商户信息详情")
    @GetMapping("/getMerchantInfo")
    public BaseResponse getMerchantInfo(@RequestParam @ApiParam String id) {
        return ResultUtil.success(merchantService.getMerchantInfo(id));
    }

    @ApiOperation(value = "根据商户Id查询商户审核信息详情")
    @GetMapping("/getMerchantAuditInfo")
    public BaseResponse getMerchantAuditInfo(@RequestParam @ApiParam String id) {
        return ResultUtil.success(merchantService.getMerchantAuditInfo(id));
    }

    @ApiOperation(value = "审核商户信息接口")
    @GetMapping("/auditMerchant")
    public BaseResponse auditMerchant(@RequestParam @ApiParam String merchantId, @RequestParam @ApiParam Boolean enabled, @RequestParam(required = false) @ApiParam String remark) {
        return ResultUtil.success(merchantService.auditMerchant(this.getSysUserVO().getUsername(), merchantId, enabled, remark));
    }

    @ApiOperation(value = "代理商下拉框")
    @GetMapping("/getAllAgent")
    public BaseResponse getAllAgent(@RequestParam @ApiParam String merchantType,@RequestParam @ApiParam String agentType) {
        return ResultUtil.success(merchantService.getAllAgent(merchantType,agentType));
    }

    @ApiOperation(value = "禁用启用商户")
    @GetMapping("/banMerchant")
    public BaseResponse banMerchant(@RequestParam @ApiParam String merchantId, @RequestParam @ApiParam Boolean enabled) {
        return ResultUtil.success(merchantService.banMerchant(this.getSysUserVO().getUsername(), merchantId, enabled));
    }

}
