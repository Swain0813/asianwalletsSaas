package com.asianwallets.permissions.controller;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.MerchantExportVO;
import com.asianwallets.permissions.feign.base.MerchantFeign;
import com.asianwallets.permissions.service.ExportService;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-27 10:03
 **/
@RestController
@Api(description = "商户管理接口")
@RequestMapping("/merchant")
public class MerchantFeignController extends BaseController {

    @Autowired
    private MerchantFeign merchantFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ExportService exportService;

    @ApiOperation(value = "添加商户")
    @PostMapping("addMerchant")
    public BaseResponse addMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(merchantDTO),
                "添加商户"));
        return merchantFeign.addMerchant(merchantDTO);
    }

    @ApiOperation(value = "修改商户")
    @PostMapping("updateMerchant")
    public BaseResponse updateMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(merchantDTO),
                "修改商户"));
        return merchantFeign.updateMerchant(merchantDTO);
    }

    @ApiOperation(value = "分页查询商户信息列表")
    @PostMapping("/pageFindMerchant")
    public BaseResponse pageFindMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantDTO),
                "分页查询商户信息列表"));
        return merchantFeign.pageFindMerchant(merchantDTO);
    }

    @ApiOperation(value = "导出商户")
    @PostMapping("/exportMerchant")
    public BaseResponse exportMerchant(@RequestBody @ApiParam MerchantDTO merchantDTO){
        ExcelWriter writer = null;
        BaseResponse baseResponse = merchantFeign.exportMerchant(merchantDTO);
        ArrayList<LinkedHashMap> data = (ArrayList<LinkedHashMap>) baseResponse.getData();
        if (data == null || data.size() == 0) {//数据不存在的场合
            HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
            writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
            return ResultUtil.success();
        }
        ArrayList<MerchantExportVO> merchantExportVOS = new ArrayList<>();
        for (LinkedHashMap datum : data) {
            merchantExportVOS.add(JSON.parseObject(JSON.toJSONString(datum), MerchantExportVO.class));
        }
        try {
            writer = exportService.getMerchantExcel(merchantExportVOS, MerchantExportVO.class);
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }


    @ApiOperation(value = "分页查询商户审核信息列表")
    @PostMapping("/pageFindMerchantAudit")
    public BaseResponse pageFindMerchantAudit(@RequestBody @ApiParam MerchantDTO merchantDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantDTO),
                "分页查询商户审核信息列表"));
        return merchantFeign.pageFindMerchantAudit(merchantDTO);
    }

    @ApiOperation(value = "根据商户Id查询商户信息详情")
    @GetMapping("/getMerchantInfo")
    public BaseResponse getMerchantInfo(@RequestParam @ApiParam String id) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(id),
                "根据商户Id查询商户信息详情"));
        return merchantFeign.getMerchantInfo(id);
    }

    @ApiOperation(value = "根据商户Id查询商户审核信息详情")
    @GetMapping("/getMerchantAuditInfo")
    public BaseResponse getMerchantAuditInfo(@RequestParam @ApiParam String id) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(id),
                "根据商户Id查询商户审核信息详情"));
        return merchantFeign.getMerchantAuditInfo(id);
    }

    @ApiOperation(value = "审核商户信息接口")
    @GetMapping("/auditMerchant")
    public BaseResponse auditMerchant(@RequestParam @ApiParam String merchantId, @RequestParam @ApiParam Boolean enabled, @RequestParam(required = false) @ApiParam String remark) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(this.getRequest().getParameterMap()),
                "审核商户信息接口"));
        return merchantFeign.auditMerchant(merchantId, enabled, remark);
    }

    @ApiOperation(value = "代理商下拉框")
    @GetMapping("/getAllAgent")
    public BaseResponse getAllAgent(@RequestParam("merchantType") @ApiParam String merchantType) {
        return merchantFeign.getAllAgent(merchantType);
    }

    @ApiOperation(value = "禁用启用商户")
    @GetMapping("/banMerchant")
    public BaseResponse banMerchant(@RequestParam("merchantId") @ApiParam String merchantId, @RequestParam("enabled") @ApiParam Boolean enabled) {
        return  merchantFeign.banMerchant(merchantId, enabled);
    }
}
