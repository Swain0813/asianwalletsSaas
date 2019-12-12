package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.WechatService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.wechat.*;
import com.asianwallets.common.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @description: wechat
 * @author: XuWenQi
 * @create: 2019-06-25 10:14
 **/
@RestController
@Api(description = "wechat")
@RequestMapping("/wechat")
public class WechatController extends BaseController {

    @Autowired
    private WechatService wechatService;

    @ApiOperation("微信BSC接口")
    @PostMapping("wechatBSC")
    public BaseResponse wechatOfflineBSC(@RequestBody @ApiParam @Valid WechatBSCDTO wechatBSCDTO) {
        return wechatService.wechatBSC(wechatBSCDTO);
    }

    @ApiOperation("微信CSB接口")
    @PostMapping("wechatCSB")
    public BaseResponse wechatOfflineCSB(@RequestBody @ApiParam @Valid WechatCSBDTO wechatCSBDTO) {
        return wechatService.wechatCSB(wechatCSBDTO);
    }

    @ApiOperation("微信退款接口")
    @PostMapping("wechatRefund")
    public BaseResponse wechatRefund(@RequestBody @ApiParam @Valid WechaRefundDTO wechaRefundDTO) {
        return wechatService.wechatRefund(wechaRefundDTO);
    }

    @ApiOperation(value = "微信撤销接口")
    @PostMapping("wechatCancel")
    public BaseResponse wechatCancel(@RequestBody @ApiParam @Valid WechatCancelDTO wechatCancelDTO) {
        return wechatService.wechatCancel(wechatCancelDTO);
    }

    @ApiOperation("微信查询接口")
    @PostMapping("wechatQuery")
    public BaseResponse wechatQuery(@RequestBody @ApiParam @Valid WechatQueryDTO wechatQueryDTO) {
        return wechatService.wechatQuery(wechatQueryDTO);
    }
}
