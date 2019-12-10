package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.MerchantProduct;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.InstitutionExportVO;
import com.asianwallets.common.vo.MerchantExportVO;
import com.asianwallets.common.vo.MerchantProductExportVO;
import com.asianwallets.permissions.feign.base.MerchantProductFeign;
import com.asianwallets.permissions.service.ExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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

    @ApiOperation(value = "添加商户产品")
    @PostMapping("/addMerchantProduct")
    public BaseResponse addMerchantProduct(@RequestBody @ApiParam List<MerchantProductDTO> merchantProductDTOs) {
        return merchantProductFeign.addMerchantProduct(merchantProductDTOs);
    }

    @ApiOperation(value = "修改商户产品")
    @PostMapping("/updateMerchantProduct")
    public BaseResponse updateMerchantProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        return merchantProductFeign.updateMerchantProduct(merchantProductDTO);
    }

    @ApiOperation(value = "批量审核商户产品")
    @PostMapping("/auditMerchantProduct")
    public BaseResponse auditMerchantProduct(@RequestBody @ApiParam AuaditProductDTO auaditProductDTO) {
        return merchantProductFeign.auditMerchantProduct(auaditProductDTO);
    }

    @ApiOperation(value = "商户分配通道")
    @PostMapping("/allotMerProductChannel")
    public BaseResponse allotMerProductChannel(@RequestBody @ApiParam @Valid MerProDTO merProDTO) {
        return merchantProductFeign.allotMerProductChannel(merProDTO);
    }

    @ApiOperation(value = "分页查询商户产品信息")
    @PostMapping("/pageFindMerProduct")
    public BaseResponse pageFindMerProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        return merchantProductFeign.pageFindMerProduct(merchantProductDTO);
    }

    @ApiOperation(value = "根据产品Id查询商户产品详情")
    @GetMapping("/getMerProductById")
    public BaseResponse getMerProductById(@RequestParam @ApiParam String merProductId) {
        return merchantProductFeign.getMerProductById(merProductId);
    }

    @ApiOperation(value = "分页查询商户审核产品信息")
    @PostMapping("/pageFindMerProductAudit")
    public BaseResponse pageFindMerProductAudit(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        return merchantProductFeign.pageFindMerProductAudit(merchantProductDTO);
    }

    @ApiOperation(value = "根据Id查询商户产品审核详情")
    @GetMapping("/getMerProductAuditById")
    public BaseResponse getMerProductAuditById(@RequestParam @ApiParam String merProductId) {
        return merchantProductFeign.getMerProductAuditById(merProductId);
    }

    @ApiOperation(value = "分页查询商户产品通道管理信息")
    @PostMapping("/pageFindMerProChannel")
    public BaseResponse pageFindMerProChannel(@RequestBody @ApiParam SearchChannelDTO searchChannelDTO) {
        return merchantProductFeign.pageFindMerProChannel(searchChannelDTO);
    }

    @ApiOperation(value = "修改机构通道")
    @PostMapping("/updateMerchantChannel")
    public BaseResponse updateMerchantChannel(@RequestBody @ApiParam BatchUpdateSortDTO batchUpdateSort) {
        return merchantProductFeign.updateMerchantChannel(batchUpdateSort);
    }

    @ApiOperation(value = "查询商户分配通道关联关系")
    @GetMapping("/getRelevantInfo")
    public BaseResponse getRelevantInfo(@RequestParam @ApiParam String merchantId) {
        return merchantProductFeign.getRelevantInfo(merchantId);
    }

    @ApiOperation(value = "导出商户产品信息")
    @PostMapping("/exportMerProduct")
    public BaseResponse exportMerProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
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


}
