package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.InstitutionExportVO;
import com.asianwallets.permissions.feign.base.InstitutionFeign;
import com.asianwallets.permissions.service.ExportService;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-27 10:02
 **/
@RestController
@Api(description = "机构管理接口")
@RequestMapping("/institution")
public class InstitutionFeignController extends BaseController {

    @Autowired
    private InstitutionFeign institutionFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ExportService exportService;


    @ApiOperation(value = "添加机构")
    @PostMapping("addInstitution")
    public BaseResponse addInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(institutionDTO),
                "添加机构"));
        return institutionFeign.addInstitution(institutionDTO);
    }


    @ApiOperation(value = "修改机构")
    @PostMapping("updateInstitution")
    public BaseResponse updateInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(institutionDTO),
                "修改机构"));
        return institutionFeign.updateInstitution(institutionDTO);
    }

    @ApiOperation(value = "分页查询机构信息列表")
    @PostMapping("/pageFindInstitution")
    public BaseResponse pageFindInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionDTO),
                "分页查询机构信息列表"));
        return institutionFeign.pageFindInstitution(institutionDTO);
    }

    @ApiOperation(value = "分页查询机构审核信息列表")
    @PostMapping("/pageFindInstitutionAudit")
    public BaseResponse pageFindInstitutionAudit(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionDTO),
                "分页查询机构审核信息列表"));
        return institutionFeign.pageFindInstitutionAudit(institutionDTO);
    }

    @ApiOperation(value = "根据机构Id查询机构信息详情")
    @GetMapping("/getInstitutionInfo")
    public BaseResponse getInstitutionInfo(@RequestParam @ApiParam String id) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(id),
                "根据机构Id查询机构信息详情"));
        return institutionFeign.getInstitutionInfo(id);
    }


    @ApiOperation(value = "根据机构Id查询机构审核信息详情")
    @GetMapping("/getInstitutionInfoAudit")
    public BaseResponse getInstitutionInfoAudit(String id) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(id),
                "根据机构Id查询机构审核信息详情"));
        return institutionFeign.getInstitutionInfoAudit(id);
    }

    @ApiOperation(value = "审核机构信息接口")
    @GetMapping("/auditInstitution")
    public BaseResponse auditInstitution(@RequestParam @ApiParam String institutionId, @RequestParam @ApiParam Boolean enabled, @RequestParam(required = false) @ApiParam String remark) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(this.getRequest().getParameterMap()),
                "审核机构信息接口"));
        return institutionFeign.auditInstitution(institutionId, enabled, remark);
    }

    @ApiOperation(value = "机构下拉框")
    @GetMapping("/getAllInstitution")
    public BaseResponse getAllInstitution() {
        return institutionFeign.getAllInstitution();
    }

    @ApiOperation(value = "导出机构")
    @GetMapping("/exportInstitution")
    public BaseResponse exportInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        BaseResponse baseResponse = institutionFeign.exportInstitution(institutionDTO);
        ArrayList<LinkedHashMap> data = (ArrayList<LinkedHashMap>) baseResponse.getData();
        if (data == null || data.size() == 0) {//数据不存在的场合
            throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
        }
        ArrayList<InstitutionExportVO> institutionExportVOS = new ArrayList<>();
        for (LinkedHashMap datum : data) {
            institutionExportVOS.add(JSON.parseObject(JSON.toJSONString(datum), InstitutionExportVO.class));
        }
        ExcelWriter writer = null;
        try {
            writer = exportService.getInstitutionExcel(institutionExportVOS, InstitutionExportVO.class);
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }


}
