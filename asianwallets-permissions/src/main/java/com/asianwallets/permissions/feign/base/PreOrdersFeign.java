package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.PreOrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.ExportPreOrdersVO;
import com.asianwallets.permissions.feign.base.impl.PreOrdersFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "asianwallets-base", fallback = PreOrdersFeignImpl.class)
public interface PreOrdersFeign {

    @ApiOperation(value = "分页查询预授权订单信息")
    @PostMapping("/preOrders/pageFindPreOrders")
    BaseResponse pageFindPreOrders(@RequestBody @ApiParam PreOrdersDTO preOrdersDTO);

    @ApiOperation(value = "查询预授权订单详情信息")
    @PostMapping("/preOrders/getPreOrdersDetail")
    BaseResponse getPreOrdersDetail(@RequestBody @ApiParam PreOrdersDTO preOrdersDTO);

    @ApiOperation(value = "预授权订单导出")
    @PostMapping("/preOrders/exportPreOrders")
    List<ExportPreOrdersVO> exportPreOrders(@RequestBody @ApiParam PreOrdersDTO preOrdersDTO);
}
