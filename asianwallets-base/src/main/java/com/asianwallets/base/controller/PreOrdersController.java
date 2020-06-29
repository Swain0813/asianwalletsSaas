package com.asianwallets.base.controller;
import com.asianwallets.base.service.PreOrdersService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.PreOrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.ExportPreOrdersVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(description = "预授权订单接口")
@RequestMapping("/preOrders")
public class PreOrdersController extends BaseController {

    @Autowired
    private PreOrdersService preOrdersService;

    @ApiOperation(value = "分页查询预授权订单信息")
    @PostMapping("/pageFindPreOrders")
    public BaseResponse pageFindPreOrders(@RequestBody @ApiParam PreOrdersDTO preOrdersDTO) {
        return ResultUtil.success(preOrdersService.pageFindPreOrders(preOrdersDTO));
    }

    @ApiOperation(value = "查询预授权订单详情信息")
    @PostMapping("/getPreOrdersDetail")
    public BaseResponse getPreOrdersDetail(@RequestBody @ApiParam PreOrdersDTO preOrdersDTO) {
        return ResultUtil.success(preOrdersService.getPreOrdersDetail(preOrdersDTO));
    }

    @ApiOperation(value = "预授权订单导出")
    @PostMapping("/exportPreOrders")
    public List<ExportPreOrdersVO> exportPreOrders(@RequestBody @ApiParam PreOrdersDTO preOrdersDTO) {
        return preOrdersService.exportPreOrders(preOrdersDTO);
    }
}
