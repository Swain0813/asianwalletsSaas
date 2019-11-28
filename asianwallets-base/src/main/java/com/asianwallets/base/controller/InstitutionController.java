package com.asianwallets.base.controller;
import com.asianwallets.base.service.InstitutionService;
import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.asianwallets.common.base.BaseController;

/**
 * <p>
 * 机构表 前端控制器
 * </p>
 *
 * @author yx
 * @since 2019-11-22
 */
@RestController
@RequestMapping("/institution")
@Api(description = "机构管理")
public class InstitutionController extends BaseController {

    @Autowired
    private InstitutionService institutionService;


    @ApiOperation(value = "添加机构")
    @PostMapping("addInstitution")
    public BaseResponse addInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        return ResultUtil.success(institutionService.addInstitution(this.getSysUserVO().getName(), institutionDTO));
    }


    @ApiOperation(value = "修改机构")
    @PostMapping("updateInstitution")
    public BaseResponse updateInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        return ResultUtil.success(institutionService.updateInstitution(this.getSysUserVO().getName(), institutionDTO));
    }

    @ApiOperation(value = "分页查询机构信息列表")
    @PostMapping("/pageFindInstitution")
    public BaseResponse pageFindInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        return ResultUtil.success(institutionService.pageFindInstitution(institutionDTO));
    }

    @ApiOperation(value = "导出机构")
    @PostMapping("/exportInstitution")
    public BaseResponse exportInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO){
        return ResultUtil.success(institutionService.exportInstitution(institutionDTO));
    }


    @ApiOperation(value = "分页查询机构审核信息列表")
    @PostMapping("/pageFindInstitutionAudit")
    public BaseResponse pageFindInstitutionAudit(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        return ResultUtil.success(institutionService.pageFindInstitutionAudit(institutionDTO));
    }

    @ApiOperation(value = "根据机构Id查询机构信息详情")
    @GetMapping("/getInstitutionInfo")
    public BaseResponse getInstitutionInfo(@RequestParam @ApiParam String id) {
        return ResultUtil.success(institutionService.getInstitutionInfo(id));
    }


    @ApiOperation(value = "根据机构Id查询机构审核信息详情")
    @GetMapping("/getInstitutionInfoAudit")
    public BaseResponse getInstitutionInfoAudit(String id) {
        return ResultUtil.success(institutionService.getInstitutionInfoAudit(id));
    }

    @ApiOperation(value = "审核机构信息接口")
    @GetMapping("/auditInstitution")
    public BaseResponse auditInstitution(@RequestParam @ApiParam String institutionId, @RequestParam @ApiParam Boolean enabled, @RequestParam(required = false) @ApiParam String remark) {
        return ResultUtil.success(institutionService.auditInstitution(this.getSysUserVO().getUsername(), institutionId, enabled, remark));
    }

    @ApiOperation(value = "机构下拉框")
    @GetMapping("/getAllInstitution")
    public BaseResponse getAllInstitution() {
        return ResultUtil.success(institutionService.getAllInstitution());
    }

    @ApiOperation(value = "禁用启用机构")
    @GetMapping("/banInstitution")
    public BaseResponse banInstitution(@RequestParam @ApiParam String institutionId, @RequestParam @ApiParam Boolean enabled) {
        return ResultUtil.success(institutionService.banInstitution(this.getSysUserVO().getUsername(), institutionId, enabled));
    }



}
