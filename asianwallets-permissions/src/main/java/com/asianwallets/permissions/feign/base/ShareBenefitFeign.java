package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.ExportAgencyShareBenefitDTO;
import com.asianwallets.common.dto.QueryAgencyShareBenefitDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.QueryAgencyShareBenefitVO;
import com.asianwallets.permissions.feign.base.impl.ShareBenefitFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 分润的feign端
 */
@FeignClient(value = "asianwallets-base", fallback = ShareBenefitFeignImpl.class)
public interface ShareBenefitFeign {

    @ApiOperation(value = "机构后台分润查询")
    @PostMapping("/share/getAgencyShareBenefit")
    BaseResponse getAgencyShareBenefit(@RequestBody @ApiParam @Valid QueryAgencyShareBenefitDTO queryAgencyShareBenefitDTO);

    @ApiOperation(value = "机构后台分润导出")
    @PostMapping("/share/exportAgencyShareBenefit")
    List<QueryAgencyShareBenefitVO> exportAgencyShareBenefit(@RequestBody @ApiParam @Valid ExportAgencyShareBenefitDTO exportAgencyShareBenefitDTO);
}
