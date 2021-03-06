package com.asianwallets.base.controller;
import com.asianwallets.base.service.AccountService;
import com.asianwallets.base.service.SettleOrderService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.SettleOrder;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 结算交易相关接口
 */
@RestController
@Api(description = "结算交易接口")
@RequestMapping("/settleorders")
public class SettleOrderController extends BaseController {

    @Autowired
    private SettleOrderService settleOrderService;

    @Autowired
    private AccountService accountService;


    @ApiOperation(value = "分页查询结算交易一览查询")
    @PostMapping("/pageSettleOrder")
    public BaseResponse pageSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO) {
        return ResultUtil.success(settleOrderService.pageSettleOrder(settleOrderDTO));
    }

    @ApiOperation(value = "分页查询集团结算交易一览查询")
    @PostMapping("/pageGroupSettleOrder")
    public BaseResponse pageGroupSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO) {
        return ResultUtil.success(settleOrderService.pageGroupSettleOrder(settleOrderDTO));
    }


    @ApiOperation(value = "分页查询结算交易详情")
    @PostMapping("/pageSettleOrderDetail")
    public BaseResponse pageSettleOrderDetail(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO) {
        return ResultUtil.success(settleOrderService.pageSettleOrderDetail(settleOrderDTO));
    }

    @ApiOperation(value = "结算审核导出")
    @PostMapping("/exportSettleOrder")
    public List<SettleOrder> exportSettleOrder(@RequestBody @ApiParam SettleOrderDTO settleOrderDTO) {
        return settleOrderService.exportSettleOrder(settleOrderDTO);
    }

    @ApiOperation(value = "结算审核")
    @PostMapping("/reviewSettlement")
    public BaseResponse reviewSettlement(@RequestBody @ApiParam ReviewSettleDTO reviewSettleDTO) {
        reviewSettleDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(settleOrderService.reviewSettlement(reviewSettleDTO));
    }

    @ApiOperation(value = "集团商户结算审核")
    @PostMapping("/reviewGroupSettlement")
    public BaseResponse reviewGroupSettlement(@RequestBody @ApiParam GroupReviewSettleDTO groupReviewSettleDTO) {
        groupReviewSettleDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(settleOrderService.reviewGroupSettlement(groupReviewSettleDTO));
    }

    @ApiOperation(value = "提款设置")
    @PostMapping("/updateAccountSettle")
    public BaseResponse updateAccountSettle(@RequestBody @Valid @ApiParam AccountSettleDTO accountSettleDTO) {
        accountSettleDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(accountService.updateAccountSettle(accountSettleDTO));
    }

    @ApiOperation(value = "手动提款")
    @PostMapping("/withdrawal")
    public BaseResponse withdrawal(@RequestBody @ApiParam WithdrawalDTO withdrawalDTO) {
        return ResultUtil.success(settleOrderService.withdrawal(withdrawalDTO,this.getSysUserVO().getUsername()));
    }
}
