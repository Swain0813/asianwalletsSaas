package com.asianwallets.baseinfo.controller;

import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-21 14:50
 **/
@RestController
@Api(description = "TestController")
public class TestController {

    @ApiOperation(value = "test")
    @GetMapping("/test")
    public BaseResponse test() {
        return ResultUtil.success("ok");
    }

}
