package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.PreOrdersDTO;
import com.asianwallets.common.entity.PreOrders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.ExportPreOrdersVO;
import com.asianwallets.permissions.feign.base.PreOrdersFeign;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(description = "预授权订单接口")
@RequestMapping("/preOrders")
@Slf4j
public class PreOrdersFeignController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private PreOrdersFeign preOrdersFeign;

    @Autowired
    private ExportService exportService;


    @ApiOperation(value = "分页查询预授权订单信息")
    @PostMapping("/pageFindPreOrders")
    public BaseResponse pageFindPreOrders(@RequestBody @ApiParam PreOrdersDTO preOrdersDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(preOrdersDTO),
                "分页查询预授权订单信息"));
        return ResultUtil.success(preOrdersFeign.pageFindPreOrders(preOrdersDTO));
    }

    @ApiOperation(value = "查询预授权订单详情信息")
    @PostMapping("/getPreOrdersDetail")
    public BaseResponse getPreOrdersDetail(@RequestBody @ApiParam PreOrdersDTO preOrdersDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(preOrdersDTO),
                "查询预授权订单详情信息"));
        return ResultUtil.success(preOrdersFeign.getPreOrdersDetail(preOrdersDTO));
    }

    @ApiOperation(value = "预授权订单导出")
    @PostMapping("/exportPreOrders")
    public BaseResponse exportPreOrders(@RequestBody @ApiParam PreOrdersDTO preOrdersDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(),
                AsianWalletConstant.SELECT, JSON.toJSONString(preOrdersDTO),
                "预授权订单导出"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<ExportPreOrdersVO> lists = preOrdersFeign.exportPreOrders(preOrdersDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(lists)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            writer=exportService.exportPreOrders(lists, ExportPreOrdersVO.class);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【预授权订单导出】==========【预授权订单导出失败】", e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }
}
