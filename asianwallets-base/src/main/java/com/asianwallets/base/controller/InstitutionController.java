package com.asianwallets.base.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.asianwallets.common.base.BaseController;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 机构表 前端控制器
 * </p>
 *
 * @author yx
 * @since 2019-11-22
 */
@RestController
@RequestMapping("/institution")
@Api(description = "机构")
public class InstitutionController extends BaseController {

}
