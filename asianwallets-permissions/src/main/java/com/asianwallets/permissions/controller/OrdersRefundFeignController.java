package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.OrdersRefundExportVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import com.asianwallets.permissions.feign.base.OrdersRefundFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(description = "订单接口")
@RequestMapping("/ordersRefund")
@Slf4j
public class OrdersRefundFeignController extends BaseController {

    @Autowired
    private OrdersRefundFeign ordersRefundFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "分页查询退款订单信息")
    @PostMapping("pageFindOrdersRefund")
    public BaseResponse pageFindOrdersRefund(@RequestBody @ApiParam OrdersRefundDTO ordersRefundDTO) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(ordersRefundDTO),
                "分页查询退款订单信息"));
        return ordersRefundFeign.pageFindOrdersRefund(ordersRefundDTO);
    }

    @ApiOperation(value = "查询退款订单详情信息")
    @GetMapping("getOrdersRefundDetail")
    public BaseResponse getOrdersRefundDetail(@RequestParam("refundId") @ApiParam String refundId) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(refundId),
                "查询退款订单详情信息"));
        return ordersRefundFeign.getOrdersRefundDetail(refundId);
    }

    @ApiOperation(value = "导出退款")
    @PostMapping("exportOrdersRefund")
    public BaseResponse exportOrdersRefund(@RequestBody @ApiParam OrdersRefundDTO ordersRefundDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName().getUsername(),
                AsianWalletConstant.SELECT, JSON.toJSONString(ordersRefundDTO),
                "导出退款"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<OrdersRefundVO> lists = ordersRefundFeign.exportOrdersRefund(ordersRefundDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(lists)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils excelUtils = new ExcelUtils();
            excelUtils.exportExcel(lists, OrdersRefundExportVO.class, writer);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出退款】==========【导出退款失败】", e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }
}