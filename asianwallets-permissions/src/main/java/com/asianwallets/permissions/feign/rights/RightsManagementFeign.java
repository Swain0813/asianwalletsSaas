package com.asianwallets.permissions.feign.rights;

import com.asianwallets.common.dto.InstitutionRightsDTO;
import com.asianwallets.common.dto.InstitutionRightsExportDTO;
import com.asianwallets.common.dto.InstitutionRightsPageDTO;
import com.asianwallets.common.dto.InstitutionRightsQueryDTO;
import com.asianwallets.common.entity.InstitutionRights;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.InstitutionRightsVO;
import com.asianwallets.permissions.feign.rights.impl.RightsManagementFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "asianwallets-rights", fallback = RightsManagementFeignImpl.class)
public interface RightsManagementFeign {

    @ApiOperation(value = "新增机构权益")
    @PostMapping("/rightsManagement/addRights")
    BaseResponse addRights(@RequestBody @ApiParam InstitutionRightsDTO institutionRightsDTO);

    @ApiOperation(value = "分页查询机构权益")
    @PostMapping("/rightsManagement/pageRightsInfo")
    BaseResponse pageRightsInfo(@RequestBody @ApiParam InstitutionRightsPageDTO institutionRightsDTO);

    @ApiOperation(value = "查询机构权益详情")
    @PostMapping("/rightsManagement/selectRightsInfo")
    BaseResponse selectRightsInfo(@RequestBody @ApiParam InstitutionRightsDTO institutionRightsDTO);

    @ApiOperation(value = "修改机构权益")
    @PostMapping("/rightsManagement/updateRightsInfo")
    BaseResponse updateRightsInfo(@RequestBody @ApiParam InstitutionRightsDTO institutionRightsDTO);

    @ApiOperation(value = "导出机构权益")
    @PostMapping("/rightsManagement/exportRightsInfo")
    List<InstitutionRightsVO> exportRightsInfo(@RequestBody @ApiParam InstitutionRightsExportDTO institutionRightsDTO);

    @ApiOperation(value = "导入机构权益")
    @PostMapping("/rightsManagement/importRightsInfo")
    BaseResponse importRightsInfo(@RequestBody @ApiParam List<InstitutionRights> institutionRights);

    @ApiOperation(value = "权益发放管理用查询机构权益信息")
    @PostMapping("/rightsManagement/getRightsInfoLists")
    List<InstitutionRights> getRightsInfoLists();

    @ApiOperation(value = "新增查询机构权益信息下拉框用")
    @PostMapping("/rightsManagement/pageRightsInfoList")
    BaseResponse pageRightsInfoList(@RequestBody @Valid @ApiParam InstitutionRightsQueryDTO institutionRightsQueryDTO);
}
