package com.asianwallets.rights.controller;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.RightsOrdersVO;
import com.asianwallets.rights.service.RightsOrdersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Api(description = "权益核销")
@RestController
@RequestMapping("/rightsOrders")
public class RightsOrdersController extends BaseController {

    @Autowired
    private RightsOrdersService rightsOrdersService;


    @ApiOperation(value = "权益核销分页查询")
    @PostMapping("pageRightsOrders")
    public BaseResponse pageRightsOrders(@RequestBody @ApiParam RightsOrdersDTO rightsOrdersDTO) {
        return ResultUtil.success(rightsOrdersService.pageRightsOrders(rightsOrdersDTO));
    }

    @ApiOperation(value = "查询核销详情")
    @PostMapping("getRightsOrdersInfo")
    public BaseResponse getRightsOrdersInfo(@RequestBody @ApiParam RightsOrdersDTO rightsOrdersDTO) {
        return ResultUtil.success(rightsOrdersService.getRightsOrdersInfo(rightsOrdersDTO));
    }

    @ApiOperation(value = "导出权益核销")
    @PostMapping("exportRightsOrders")
    public List<RightsOrdersVO> exportRightsOrders(@RequestBody @ApiParam RightsOrdersExportDTO rightsOrdersDTO) {
        return rightsOrdersService.exportRightsOrders(rightsOrdersDTO);
    }

    @ApiOperation(value = "权益核销查询API")
    @PostMapping("selectRightsOrders")
    public BaseResponse selectRightsOrders(@RequestBody @ApiParam RightsOrdersOutDTO rightsOrdersDTO) {
        return ResultUtil.success(rightsOrdersService.selectRightsOrders(rightsOrdersDTO));
    }

    @ApiOperation(value = "权益核销API")
    @PostMapping("verificationCancel")
    @CrossOrigin
    public BaseResponse verificationCancel(@RequestBody @ApiParam VerificationCancleDTO verificationCancleDTO) {
        return rightsOrdersService.verificationCancel(verificationCancleDTO);
    }

    @ApiOperation(value = "权益退款(发放平台)API")
    @PostMapping("sysRightsRefund")
    public BaseResponse sysRightsRefund(@RequestBody @ApiParam RightsRefundDTO rightsRefundDTO) {
        return rightsOrdersService.sysRightsRefund(rightsRefundDTO);
    }
    @ApiOperation(value = "权益退款(机构)API")
    @PostMapping("insRightsRefund")
    public BaseResponse insRightsRefund(@RequestBody @ApiParam RightsRefundDTO rightsRefundDTO) {
        return rightsOrdersService.insRightsRefund(rightsRefundDTO);
    }



}
