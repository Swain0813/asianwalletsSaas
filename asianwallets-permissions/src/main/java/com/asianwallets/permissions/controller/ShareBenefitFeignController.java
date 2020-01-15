package com.asianwallets.permissions.controller;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.ExportAgencyShareBenefitDTO;
import com.asianwallets.common.dto.QueryAgencyShareBenefitDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.QueryAgencyShareBenefitEnVO;
import com.asianwallets.common.vo.QueryAgencyShareBenefitVO;
import com.asianwallets.permissions.feign.base.ShareBenefitFeign;
import com.asianwallets.permissions.service.ExportService;
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
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(description = "分润功能接口")
@RequestMapping("/share")
@Slf4j
public class ShareBenefitFeignController extends BaseController {

    @Autowired
    private ShareBenefitFeign shareBenefitFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ExportService exportService;

    @ApiOperation(value = "机构后台分润查询")
    @PostMapping("/getAgencyShareBenefit")
    public BaseResponse getAgencyShareBenefit(@RequestBody @ApiParam @Valid QueryAgencyShareBenefitDTO queryAgencyShareBenefitDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(queryAgencyShareBenefitDTO),
                "机构后台分润查询"));
        return shareBenefitFeign.getAgencyShareBenefit(queryAgencyShareBenefitDTO);
    }

    @ApiOperation(value = "机构后台分润导出")
    @PostMapping("/exportAgencyShareBenefit")
    public BaseResponse exportAgencyShareBenefit(@RequestBody @ApiParam ExportAgencyShareBenefitDTO exportAgencyShareBenefitDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(exportAgencyShareBenefitDTO),
                "机构后台分润导出"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try{
            List<QueryAgencyShareBenefitVO> dataList = shareBenefitFeign.exportAgencyShareBenefit(exportAgencyShareBenefitDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(dataList)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            if (AsianWalletConstant.EN_US.equals(this.getLanguage())) {
                //英文的场合
                writer = exportService.exportAgencyShareBenefit(dataList, QueryAgencyShareBenefitEnVO.class);
            } else {
                writer = exportService.exportAgencyShareBenefit(dataList, QueryAgencyShareBenefitVO.class);
            }
            writer.flush(out);
        }catch (Exception e) {
            log.info("==========【机构后台分润导出】==========【机构后台分润导出导出异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

}
