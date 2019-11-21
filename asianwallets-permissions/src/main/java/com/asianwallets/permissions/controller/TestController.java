package com.asianwallets.permissions.controller;

import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.TestFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-21 14:50
 **/
@RestController
@Api(description = "TestController")
@RequestMapping("/per")
public class TestController {

    @Autowired
    TestFeign testFeign;

    @ApiOperation(value = "test")
    @GetMapping("/test")
    public BaseResponse test() {
        return testFeign.test();
    }

}
