package com.asianwallets.permissions.controller;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.MerchantProduct;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.*;
import com.asianwallets.permissions.feign.base.MerchantProductFeign;
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
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-10 17:03
 **/
@RestController
@Api(description = "商户产品管理接口")
@RequestMapping("/merchantProduct")
public class MerchantProductFeignController extends BaseController {

    @Autowired
    private MerchantProductFeign merchantProductFeign;

    @Autowired
    private ExportService exportService;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "添加商户产品")
    @PostMapping("/addMerchantProduct")
    public BaseResponse addMerchantProduct(@RequestBody @ApiParam List<MerchantProductDTO> merchantProductDTOs) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(merchantProductDTOs),
                "添加商户产品"));
        return merchantProductFeign.addMerchantProduct(merchantProductDTOs);
    }

    @ApiOperation(value = "修改商户产品")
    @PostMapping("/updateMerchantProduct")
    public BaseResponse updateMerchantProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(merchantProductDTO),
                "修改商户产品"));
        return merchantProductFeign.updateMerchantProduct(merchantProductDTO);
    }

    @ApiOperation(value = "批量审核商户产品")
    @PostMapping("/auditMerchantProduct")
    public BaseResponse auditMerchantProduct(@RequestBody @ApiParam AuaditProductDTO auaditProductDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(auaditProductDTO),
                "批量审核商户产品"));
        return merchantProductFeign.auditMerchantProduct(auaditProductDTO);
    }

    @ApiOperation(value = "商户分配通道")
    @PostMapping("/allotMerProductChannel")
    public BaseResponse allotMerProductChannel(@RequestBody @ApiParam @Valid MerProDTO merProDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(merProDTO),
                "商户分配通道"));
        return merchantProductFeign.allotMerProductChannel(merProDTO);
    }

    @ApiOperation(value = "分页查询商户产品信息")
    @PostMapping("/pageFindMerProduct")
    public BaseResponse pageFindMerProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantProductDTO),
                "分页查询商户产品信息"));
        return merchantProductFeign.pageFindMerProduct(merchantProductDTO);
    }

    @ApiOperation(value = "根据产品Id查询商户产品详情")
    @GetMapping("/getMerProductById")
    public BaseResponse getMerProductById(@RequestParam @ApiParam String merProductId) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merProductId),
                "根据产品Id查询商户产品详情"));
        return merchantProductFeign.getMerProductById(merProductId);
    }

    @ApiOperation(value = "分页查询商户审核产品信息")
    @PostMapping("/pageFindMerProductAudit")
    public BaseResponse pageFindMerProductAudit(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantProductDTO),
                "分页查询商户审核产品信息"));
        return merchantProductFeign.pageFindMerProductAudit(merchantProductDTO);
    }

    @ApiOperation(value = "根据Id查询商户产品审核详情")
    @GetMapping("/getMerProductAuditById")
    public BaseResponse getMerProductAuditById(@RequestParam @ApiParam String merProductId) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merProductId),
                "根据Id查询商户产品审核详情"));
        return merchantProductFeign.getMerProductAuditById(merProductId);
    }

    @ApiOperation(value = "分页查询商户产品通道管理信息")
    @PostMapping("/pageFindMerProChannel")
    public BaseResponse pageFindMerProChannel(@RequestBody @ApiParam SearchChannelDTO searchChannelDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(searchChannelDTO),
                "分页查询商户产品通道管理信息"));
        return merchantProductFeign.pageFindMerProChannel(searchChannelDTO);
    }

    @ApiOperation(value = "根据商户通道Id查询商户通道详情")
    @GetMapping("/getMerChannelInfoById")
    public BaseResponse getMerChannelInfoById(@RequestParam @ApiParam String merChannelId) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merChannelId),
                "根据商户通道Id查询商户通道详情"));
        return merchantProductFeign.getMerChannelInfoById(merChannelId);
    }


    @ApiOperation(value = "修改机构通道")
    @PostMapping("/updateMerchantChannel")
    public BaseResponse updateMerchantChannel(@RequestBody @ApiParam List<BatchUpdateSortDTO> batchUpdateSort) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(batchUpdateSort),
                "修改机构通道"));
        return merchantProductFeign.updateMerchantChannel(batchUpdateSort);
    }

    @ApiOperation(value = "查询商户分配通道关联关系")
    @GetMapping("/getRelevantInfo")
    public BaseResponse getRelevantInfo(@RequestParam @ApiParam String merchantId) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantId),
                "查询商户分配通道关联关系"));
        return merchantProductFeign.getRelevantInfo(merchantId);
    }

    @ApiOperation(value = "导出商户产品信息")
    @PostMapping("/exportMerProduct")
    public BaseResponse exportMerProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantProductDTO),
                "导出商户产品信息"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            ServletOutputStream out = response.getOutputStream();
            List<MerchantProduct> merchantLists = merchantProductFeign.exportMerProduct(merchantProductDTO);
            if (merchantLists == null || merchantLists.size() == 0) {//数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ArrayList<MerchantProductExportVO> merchantProExportVOS = new ArrayList<>();
            for (MerchantProduct merchantProduct : merchantLists) {
                merchantProExportVOS.add(JSON.parseObject(JSON.toJSONString(merchantProduct), MerchantProductExportVO.class));
            }
            writer = exportService.getMerchantProductExcel(merchantProExportVOS, MerchantProductExportVO.class);
            writer.flush(out);
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "导出商户通道信息")
    @PostMapping("/exportMerChannel")
    public BaseResponse exportMerChannel(@RequestBody @ApiParam SearchChannelDTO searchChannelDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(searchChannelDTO),
                "导出商户通道信息"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            ServletOutputStream out = response.getOutputStream();
            List<MerChannelVO> merChannelVOS = merchantProductFeign.exportMerChannel(searchChannelDTO);
            if (merChannelVOS == null || merChannelVOS.size() == 0) {//数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ArrayList<MerChannelExportVO> merchantChannelExportVOS = new ArrayList<>();
            for (MerChannelVO merchantProduct : merChannelVOS) {
                merchantChannelExportVOS.add(JSON.parseObject(JSON.toJSONString(merchantProduct), MerChannelExportVO.class));
            }
            writer = exportService.getMerchantChannelExcel(merchantChannelExportVOS, MerChannelExportVO.class);
            writer.flush(out);

        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "商户产品排序一览的分页查询")
    @PostMapping("/pageMerProductSort")
    public BaseResponse pageMerProductSort(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantProductDTO),
                "商户产品排序一览的分页查询"));
        return merchantProductFeign.pageMerProductSort(merchantProductDTO);
    }

    @ApiOperation(value = "批量修改商户产品排序")
    @PostMapping("/updateMerchantProductSort")
    public BaseResponse updateMerchantProductSort(@RequestBody @ApiParam List<MerchantProductDTO> merchantProductDTOLists) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(merchantProductDTOLists),
                "批量修改商户产品排序"));
        return merchantProductFeign.updateMerchantProductSort(merchantProductDTOLists);
    }

}
