package com.asianwallets.permissions.controller;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.DccReportDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.DccReportVO;
import com.asianwallets.permissions.feign.base.ReportFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
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
@Api(description = "报表接口")
@Slf4j
@RequestMapping("/report")
public class ReportFeignController extends BaseController {

    @Autowired
    private ReportFeign reportFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "DCC报表查询")
    @PostMapping("getDccReport")
    public BaseResponse getDccReport(@RequestBody @ApiParam DccReportDTO dccReportDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(dccReportDTO),
                "DCC报表查询"));
        return reportFeign.getDccReport(dccReportDTO);
    }

    @ApiOperation(value = "DCC报表导出")
    @PostMapping("/exportDccReport")
    public BaseResponse exportDccReport(@RequestBody @ApiParam DccReportDTO dccReportDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(dccReportDTO),
                "DCC报表导出"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<DccReportVO> dataList = reportFeign.exportDccReport(dccReportDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(dataList)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils excelUtils = new ExcelUtils();
            excelUtils.exportExcel(dataList, DccReportVO.class, writer);
            writer.flush(out);
        }catch (Exception e) {
            log.info("==========【DCC报表导出】==========【DCC报表导出异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }
}
