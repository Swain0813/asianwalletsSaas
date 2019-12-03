package com.asianwallets.message.controller;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.message.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 短信模块
 *
 * @author: yangshanlong@asianwallets.com on 2019/01/23.
 **/
@RestController
@Api(value = "短信发送相关接口")
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @ApiOperation(value = "国内普通发送")
    @PostMapping("/sendSimple")
    public BaseResponse sendSimple(@RequestParam(value = "mobile") @ApiParam String mobile, @RequestParam(value = "content") @ApiParam String content) {
        return ResultUtil.success(smsService.sendSimple(mobile, content));
    }

    @ApiOperation(value = "国际短信发送")
    @PostMapping("/sendInternation")
    public BaseResponse sendInternation(@RequestParam(value = "mobile") @ApiParam String mobile,@RequestParam(value = "content") @ApiParam String content) {
        return ResultUtil.success(smsService.sendInternation(mobile,content));
    }

    @ApiOperation(value = "国内普通短信模板")
    @PostMapping("/sendSimpleTemplate")
    public BaseResponse sendSimpleTemplate(@RequestParam(value = "language") @ApiParam String language,@RequestParam(value = "num") @ApiParam
            Status num,@RequestParam(value = "mobile") @ApiParam String mobile, @RequestBody Map<String, Object> content){
        return ResultUtil.success(smsService.sendSimpleTemplate(language,num,mobile,content));
    }

    @ApiOperation(value = "国际短信模板")
    @PostMapping("/sendIntTemplate")
    public BaseResponse sendIntTemplate(@RequestParam(value = "language") @ApiParam String language,@RequestParam(value = "num") @ApiParam
            Status num,@RequestParam(value = "mobile") @ApiParam String mobile, @RequestBody Map<String, Object> content){
        return ResultUtil.success(smsService.sendIntTemplate(language,num,mobile,content));
    }


}
