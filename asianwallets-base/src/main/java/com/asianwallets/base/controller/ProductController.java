package com.asianwallets.base.controller;

import com.asianwallets.base.service.ProductService;
import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.dto.ProductDTO;
import com.asianwallets.common.entity.Product;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.asianwallets.common.base.BaseController;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 产品表 前端控制器
 * </p>
 *
 * @author yx
 * @since 2019-12-05
 */
@Api(description = "产品管理")
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @ApiOperation(value = "添加产品")
    @PostMapping("/addProduct")
    public BaseResponse addProduct(@RequestBody @ApiParam ProductDTO productDTO) {
        return ResultUtil.success(productService.addProduct(this.getSysUserVO().getName(), productDTO));
    }

    @ApiOperation(value = "更新产品")
    @PostMapping("/updateProduct")
    public BaseResponse updateProduct(@RequestBody @ApiParam ProductDTO productDTO) {
        return ResultUtil.success(productService.updateProduct(this.getSysUserVO().getUsername(), productDTO));
    }

    @ApiOperation(value = "分页查询产品")
    @PostMapping("/pageProduct")
    public BaseResponse pageProduct(@RequestBody @ApiParam ProductDTO productDTO) {
        if (StringUtils.isBlank(productDTO.getLanguage())) {
            productDTO.setLanguage(this.getLanguage());
        }
        return ResultUtil.success(productService.pageProduct(productDTO));
    }

    @ApiOperation(value = "查询产品")
    @PostMapping("/selectProduct")
    public BaseResponse selectProduct(@RequestBody @ApiParam ProductDTO productDTO) {
        if (StringUtils.isBlank(productDTO.getLanguage())) {
            productDTO.setLanguage(this.getLanguage());
        }
        return ResultUtil.success(productService.selectProduct(productDTO));
    }


}
