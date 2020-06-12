package com.asianwallets.rights.controller;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.InstitutionRights;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.InstitutionRightsVO;
import com.asianwallets.rights.service.RightsManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;

@Api(description = "机构权益管理")
@RestController
@RequestMapping("/rightsManagement")
public class RightsManagementController extends BaseController {

    @Autowired
    private RightsManagementService rightsManagementService;

    @ApiOperation(value = "新增机构权益")
    @PostMapping("addRights")
    public BaseResponse addRights(@RequestBody @Valid @ApiParam InstitutionRightsDTO institutionRightsDTO) {
        return ResultUtil.success(rightsManagementService.addRights(institutionRightsDTO, this.getSysUserVO().getUsername()));
    }

    @ApiOperation(value = "新增机构权益【对外API】")
    @PostMapping("addRightsInfo")
    public BaseResponse addRightsApi(@RequestBody @Valid @ApiParam RightsApiDTO institutionRightsApiDTO) {
        return ResultUtil.success(rightsManagementService.addRightsApi(institutionRightsApiDTO));
    }

    @ApiOperation(value = "分页查询机构权益")
    @PostMapping("pageRightsInfo")
    public BaseResponse pageRightsInfo(@RequestBody @ApiParam InstitutionRightsPageDTO institutionRightsPageDTO) {
        return ResultUtil.success(rightsManagementService.pageRightsInfo(institutionRightsPageDTO));
    }

    @ApiOperation(value = "查询机构权益详情")
    @PostMapping("selectRightsInfo")
    public BaseResponse selectRightsInfo(@RequestBody @ApiParam InstitutionRightsDTO institutionRightsDTO) {
        return ResultUtil.success(rightsManagementService.selectRightsInfo(institutionRightsDTO));
    }

    @ApiOperation(value = "修改机构权益")
    @PostMapping("updateRightsInfo")
    public BaseResponse updateRightsInfo(@RequestBody @ApiParam InstitutionRightsDTO institutionRightsDTO) {
        return ResultUtil.success(rightsManagementService.updateRightsInfo(institutionRightsDTO, this.getSysUserVO().getUsername()));
    }

    @ApiOperation(value = "导出机构权益")
    @PostMapping("exportRightsInfo")
    public List<InstitutionRightsVO> exportRightsInfo(@RequestBody @ApiParam InstitutionRightsExportDTO institutionRightsExportDTO) {
        return rightsManagementService.exportRightsInfo(institutionRightsExportDTO);
    }

    @ApiOperation(value = "导入机构权益")
    @PostMapping("importRightsInfo")
    public BaseResponse importRightsInfo(@RequestBody @ApiParam List<InstitutionRights> institutionRights) {
        return ResultUtil.success(rightsManagementService.importRightsInfo(institutionRights));
    }

    @ApiOperation(value = "查询机构权益【对外API】")
    @PostMapping("getRightsInfo")
    public BaseResponse getRightsInfo(@RequestBody @Valid @ApiParam InstitutionRightsInfoApiDTO institutionRightsInfoApiDTO) {
        return ResultUtil.success(rightsManagementService.getRightsInfo(institutionRightsInfoApiDTO));
    }

    @ApiOperation(value = "权益发放管理用查询机构权益信息")
    @PostMapping("/getRightsInfoLists")
    public List<InstitutionRights> getRightsInfoLists() {
        return rightsManagementService.getRightsInfoLists();
    }


    @ApiOperation(value = "新增查询机构权益信息下拉框用")
    @PostMapping("/pageRightsInfoList")
    public BaseResponse pageRightsInfoList(@RequestBody @Valid @ApiParam InstitutionRightsQueryDTO institutionRightsQueryDTO) {
        return ResultUtil.success(rightsManagementService.pageRightsInfoList(institutionRightsQueryDTO));
    }
}
