package com.asianwallets.base.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.asianwallets.common.base.BaseController;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 机构结算单表 前端控制器
 * </p>
 *
 * @author yx
 * @since 2020-01-14
 */
@Api(description = "机构结算对账单")
@RestController
@RequestMapping("/settleCheckAccount")
public class SettleCheckAccountController extends BaseController {

}
