package com.asianwallets.base.controller;

import com.asianwallets.base.service.ShareBenefitService;
import com.asianwallets.common.dto.ExportAgencyShareBenefitDTO;
import com.asianwallets.common.dto.QueryAgencyShareBenefitDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.QueryAgencyShareBenefitVO;
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

/**
 * 分润报表相关
 */
@RestController
@Api(description = "分润服务")
@RequestMapping("/share")
public class ShareBenefitController {

    @Autowired
    private ShareBenefitService shareBenefitService;

    @ApiOperation(value = "机构后台分润查询")
    @PostMapping("/getAgencyShareBenefit")
    public BaseResponse getAgencyShareBenefit(@RequestBody @ApiParam @Valid QueryAgencyShareBenefitDTO queryAgencyShareBenefitDTO) {
        return ResultUtil.success(shareBenefitService.getAgencyShareBenefit(queryAgencyShareBenefitDTO));
    }

    @ApiOperation(value = "机构后台分润导出")
    @PostMapping("/exportAgencyShareBenefit")
    public List<QueryAgencyShareBenefitVO> exportAgencyShareBenefit(@RequestBody @ApiParam @Valid ExportAgencyShareBenefitDTO exportAgencyShareBenefitDTO) {
        return shareBenefitService.exportAgencyShareBenefit(exportAgencyShareBenefitDTO);
    }
}
