package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.DccReportDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.DccReportVO;
import com.asianwallets.common.vo.InsDailyTradeVO;
import com.asianwallets.permissions.feign.base.impl.ReportFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 报表相关接口的Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = ReportFeignImpl.class)
public interface ReportFeign {

    @ApiOperation(value = "DCC报表查询")
    @PostMapping("/report/getDccReport")
    BaseResponse getDccReport(@RequestBody @ApiParam DccReportDTO dccReportDTO);

    @ApiOperation(value = "DCC报表导出")
    @PostMapping("/report/exportDccReport")
    List<DccReportVO> exportDccReport(@RequestBody @ApiParam DccReportDTO dccReportDTO);

    @ApiOperation(value = "分页查询机构日交易汇总表")
    @PostMapping("/report/pageFindInsDailyTrade")
    BaseResponse pageFindInsDailyTrade(@RequestBody @ApiParam OrdersDTO ordersDTO);

    @ApiOperation(value = "导出机构日交易汇总表")
    @PostMapping("/report/exportInsDailyTrade")
    List<InsDailyTradeVO> exportInsDailyTrade(@RequestBody @ApiParam OrdersDTO ordersDTO);
}
