package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.MerchantReportDTO;
import com.asianwallets.common.entity.MerchantReport;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.MerchantReportVO;
import com.asianwallets.permissions.feign.base.impl.MerchantReportFeignImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

/**
 * 商户报备
 */
@FeignClient(value = "asianwallets-base", fallback = MerchantReportFeignImpl.class)
public interface MerchantReportFeign {

    @PostMapping("/merchantReport/addReport")
    BaseResponse addReport(@RequestBody @ApiParam MerchantReportDTO merchantReportDTO);

    @PostMapping("/merchantReport/pageReport")
    BaseResponse pageReport(@RequestBody @ApiParam MerchantReportDTO merchantReportDTO);

    @PostMapping("/merchantReport/updateReport")
    BaseResponse updateReport(@RequestBody @ApiParam MerchantReportDTO merchantReportDTO);

    @PostMapping("/merchantReport/banReport")
    BaseResponse banReport(@RequestBody @ApiParam MerchantReportDTO merchantReportDTO);

    @PostMapping("/merchantReport/exportReport")
    List<MerchantReportVO> exportReport(@RequestBody @ApiParam MerchantReportDTO merchantReportDTO);

    @PostMapping("/merchantReport/importMerchantReport")
    BaseResponse importMerchantReport(@RequestBody @ApiParam List<MerchantReport> merchantReportList);
}