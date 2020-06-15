package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.InstitutionRightsDTO;
import com.asianwallets.common.dto.InstitutionRightsExportDTO;
import com.asianwallets.common.dto.InstitutionRightsPageDTO;
import com.asianwallets.common.dto.InstitutionRightsQueryDTO;
import com.asianwallets.common.entity.InstitutionRights;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.InstitutionRightsExportEnVO;
import com.asianwallets.common.vo.InstitutionRightsExportVO;
import com.asianwallets.common.vo.InstitutionRightsVO;
import com.asianwallets.permissions.feign.rights.RightsManagementFeign;
import com.asianwallets.permissions.service.OperationLogService;
import com.asianwallets.permissions.service.RightsManagementFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(description = "机构权益管理")
@RequestMapping("/rightsManagement")
public class RightsManagementController extends BaseController {

    @Autowired
    private RightsManagementFeign rightsManagementFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private RightsManagementFeignService rightsManagementFeignService;


    @ApiOperation(value = "新增机构权益")
    @PostMapping("addRights")
    public BaseResponse addRights(@RequestBody @ApiParam InstitutionRightsDTO institutionRightsDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(institutionRightsDTO),
                "新增机构权益"));
        return rightsManagementFeign.addRights(institutionRightsDTO);
    }

    @ApiOperation(value = "分页查询机构权益")
    @PostMapping("pageRightsInfo")
    public BaseResponse pageRightsInfo(@RequestBody @ApiParam InstitutionRightsPageDTO institutionRightsDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionRightsDTO),
                "分页查询机构权益"));
        return rightsManagementFeign.pageRightsInfo(institutionRightsDTO);
    }

    @ApiOperation(value = "查询机构权益详情")
    @PostMapping("selectRightsInfo")
    public BaseResponse selectRightsInfo(@RequestBody @ApiParam InstitutionRightsDTO institutionRightsDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionRightsDTO),
                "查询机构权益详情"));
        return rightsManagementFeign.selectRightsInfo(institutionRightsDTO);
    }

    @ApiOperation(value = "修改机构权益")
    @PostMapping("updateRightsInfo")
    public BaseResponse updateRightsInfo(@RequestBody @ApiParam InstitutionRightsDTO institutionRightsDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(institutionRightsDTO),
                "修改机构权益"));
        return rightsManagementFeign.updateRightsInfo(institutionRightsDTO);
    }

    @ApiOperation(value = "导出机构权益")
    @PostMapping("exportRightsInfo")
    public BaseResponse exportRightsInfo(@RequestBody @ApiParam InstitutionRightsExportDTO institutionRightsDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionRightsDTO),
                "导出机构权益"));
        List<InstitutionRightsVO> list = rightsManagementFeign.exportRightsInfo(institutionRightsDTO);
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(list)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            if (TradeConstant.ZH_CN.equals(this.getLanguage())) {
                //中文的场合
                writer = rightsManagementFeignService.exportRights(list, InstitutionRightsExportVO.class, this.getLanguage());
            } else {
                writer = rightsManagementFeignService.exportRights(list, InstitutionRightsExportEnVO.class, this.getLanguage());
            }
            writer.flush(out);
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "权益发放管理用查询机构权益信息")
    @PostMapping("/getRightsInfoLists")
    public List<InstitutionRights> getRightsInfoLists() {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, null,
                "权益发放管理用查询机构权益信息"));
        return rightsManagementFeign.getRightsInfoLists();
    }

    @ApiOperation(value = "新增查询机构权益信息下拉框用")
    @PostMapping("/pageRightsInfoList")
    public BaseResponse pageRightsInfoList(@RequestBody @ApiParam InstitutionRightsQueryDTO institutionRightsDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionRightsDTO),
                "新增查询机构权益信息下拉框用"));
        return rightsManagementFeign.pageRightsInfoList(institutionRightsDTO);
    }

}
