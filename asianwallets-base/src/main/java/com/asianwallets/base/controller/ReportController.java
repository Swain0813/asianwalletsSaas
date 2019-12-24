package com.asianwallets.base.controller;
import com.asianwallets.base.service.OrdersService;
import com.asianwallets.common.dto.DccReportDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.DccReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 *报表相关接口
 */
@RestController
@Api(description = "报表接口")
@Slf4j
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private OrdersService ordersService;

    @ApiOperation(value = "DCC报表查询")
    @PostMapping("/getDccReport")
    public BaseResponse getDccReport(@RequestBody @ApiParam DccReportDTO dccReportDTO) {
        return ResultUtil.success(ordersService.getDccReport(dccReportDTO));
    }

    @ApiOperation(value = "DCC报表导出")
    @PostMapping("/exportDccReport")
    public List<DccReportVO> exportDccReport(@RequestBody @ApiParam DccReportDTO dccReportDTO) {
        return ordersService.exportDccReport(dccReportDTO);
    }
}
