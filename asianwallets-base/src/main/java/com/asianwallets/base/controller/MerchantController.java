package com.asianwallets.base.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.asianwallets.common.base.BaseController;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Api(description = "商户管理")
@RestController
@RequestMapping("/merchant")
public class MerchantController extends BaseController {

}
