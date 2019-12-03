package com.asianwallets.message.controller;
import com.asianwallets.common.enums.Status;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.message.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 短信模块
 *
 * @author: yangshanlong@asianwallets.com on 2019/01/24.
 **/
@RestController
@Api(value = "邮件发送相关接口")
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @ApiOperation(value = "发送简单邮件")
    @PostMapping("/sendSimpleMail")
    public BaseResponse sendSimpleMail(@RequestParam(value = "sendTo") @ApiParam String sendTo,
                                       @RequestParam(value = "title") @ApiParam String title,
                                       @RequestParam(value = "content") @ApiParam String content) {
        return ResultUtil.success(emailService.sendSimpleMail(sendTo, title, content));
    }

    @ApiOperation(value = "发送模板邮件")
    @PostMapping("/sendTemplateMail")
    public BaseResponse sendTemplateMail(@RequestParam(value = "sendTo") @ApiParam String sendTo, @RequestParam(value = "languageNum") @ApiParam
            String languageNum, @RequestParam(value = "templateNum") @ApiParam Status templateNum, @RequestBody Map<String, Object> param){
        return ResultUtil.success(emailService.sendTemplateMail(sendTo,languageNum,templateNum,param));
    }
}
