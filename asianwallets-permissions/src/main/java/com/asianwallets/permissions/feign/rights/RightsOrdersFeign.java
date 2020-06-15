package com.asianwallets.permissions.feign.rights;
import com.asianwallets.common.dto.RightsOrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.RightsOrdersVO;
import com.asianwallets.permissions.feign.rights.impl.RightsOrdersFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "asianwallets-rights", fallback = RightsOrdersFeignImpl.class)
public interface RightsOrdersFeign {

    @ApiOperation(value = "权益核销分页查询")
    @PostMapping("/rightsOrders/pageRightsOrders")
    BaseResponse pageRightsOrders(@RequestBody @ApiParam RightsOrdersDTO rightsOrdersDTO);

    @ApiOperation(value = "查询核销详情")
    @PostMapping("/rightsOrders/getRightsOrdersInfo")
    BaseResponse getRightsOrdersInfo(@RequestBody @ApiParam RightsOrdersDTO rightsOrdersDTO);

    @ApiOperation(value = "导出权益核销")
    @PostMapping("/rightsOrders/exportRightsOrders")
    List<RightsOrdersVO> exportRightsOrders(@RequestBody @ApiParam RightsOrdersDTO rightsOrdersDTO);
}
