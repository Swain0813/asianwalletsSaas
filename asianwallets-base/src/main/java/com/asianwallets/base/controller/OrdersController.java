package com.asianwallets.base.controller;

import com.asianwallets.base.service.OrdersService;
import com.asianwallets.common.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "订单接口")
@RequestMapping("/orders")
public class OrdersController extends BaseController {

    @Autowired
    private OrdersService ordersService;

}
