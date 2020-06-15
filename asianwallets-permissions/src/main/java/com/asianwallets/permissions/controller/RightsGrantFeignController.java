package com.asianwallets.permissions.controller;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.RightsGrantDTO;
import com.asianwallets.common.dto.RightsGrantInsertDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.ExportRightsGrantVO;
import com.asianwallets.common.vo.ExportRightsUserGrantVO;
import com.asianwallets.permissions.feign.rights.RightsGrantFeign;
import com.asianwallets.permissions.service.ExportService;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(description = "权益发放管理接口")
@RequestMapping("/rightsGrant")
public class RightsGrantFeignController extends BaseController {

    @Autowired
    private RightsGrantFeign rightsGrantFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ExportService exportService;

    @ApiOperation(value = "新增权益发放管理")
    @PostMapping("/addRightsGrant")
    public BaseResponse addRightsGrant(@RequestBody @ApiParam @Valid RightsGrantInsertDTO rightsGrantInsertDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(rightsGrantInsertDTO),
                "新增权益发放管理"));
        return rightsGrantFeign.addRightsGrant(rightsGrantInsertDTO);
    }

    @ApiOperation(value = "分页查询权益发放管理信息")
    @PostMapping("/pageFindRightsGrant")
    public BaseResponse pageFindRightsGrant(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(rightsGrantDTO),
                "分页查询权益发放管理信息"));
        return rightsGrantFeign.pageFindRightsGrant(rightsGrantDTO);
    }

    @ApiOperation(value = "导出权益发放管理信息")
    @PostMapping("/exportRightsGrants")
    public BaseResponse exportRightsGrants(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(rightsGrantDTO),
                "导出权益发放管理信息"));
        List<ExportRightsGrantVO> exportRightsGrantVOList = rightsGrantFeign.exportRightsGrants(rightsGrantDTO);
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(exportRightsGrantVOList)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            writer = exportService.getRightsGrantExcel(exportRightsGrantVOList, ExportRightsGrantVO.class);
            writer.flush(out);
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    //====================================【票券接口】===============================================================
    @ApiOperation(value = "导出权益票券信息")
    @PostMapping("/exportRightsUserGrant")
    public BaseResponse exportRightsUserGrant(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(rightsGrantDTO),
                "导出权益票券信息"));
        List<ExportRightsUserGrantVO> exportRightsUserGrantVOList = rightsGrantFeign.exportRightsUserGrant(rightsGrantDTO);
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(exportRightsUserGrantVOList)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            writer = exportService.getRightsUserGrantExcel(exportRightsUserGrantVOList, ExportRightsUserGrantVO.class);
            writer.flush(out);
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "分页查询权益票券信息")
    @PostMapping("/pageFindRightsUserGrant")
    public BaseResponse pageFindRightsUserGrant(@RequestBody @ApiParam RightsGrantDTO rightsGrantDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(rightsGrantDTO),
                "分页查询权益票券信息"));
        return rightsGrantFeign.pageFindRightsUserGrant(rightsGrantDTO);
    }

    @ApiOperation(value = "查询权益票券详情")
    @GetMapping("/getRightsUserGrantDetail")
    public BaseResponse getRightsUserGrantDetail(@RequestParam @ApiParam String ticketId) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(ticketId),
                "查询权益票券详情"));
        return rightsGrantFeign.getRightsUserGrantDetail(ticketId);
    }


}
