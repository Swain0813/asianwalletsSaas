package com.asianwallets.base.controller;

import com.asianwallets.base.service.MerchantProductService;
import com.asianwallets.common.dto.MerProDTO;
import com.asianwallets.common.dto.MerchantProductDTO;
import com.asianwallets.common.dto.ProductDTO;
import com.asianwallets.common.entity.MerchantProduct;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.asianwallets.common.base.BaseController;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yx
 * @since 2019-12-09
 */
@Api(description = "商户产品管理")
@RestController
@RequestMapping("/merchantProduct")
public class MerchantProductController extends BaseController {

    @Autowired
    private MerchantProductService merchantProductService;

    @ApiOperation(value = "添加商户产品")
    @PostMapping("/addMerchantProduct")
    public BaseResponse addMerchantProduct(@RequestBody @ApiParam List<MerchantProductDTO> merchantProductDTOs) {
        return ResultUtil.success(merchantProductService.addMerchantProduct(this.getSysUserVO().getName(), merchantProductDTOs));
    }

    @ApiOperation(value = "修改商户产品")
    @PostMapping("/updateMerchantProduct")
    public BaseResponse updateMerchantProduct(@RequestBody @ApiParam MerchantProductDTO merchantProductDTO) {
        return ResultUtil.success(merchantProductService.updateMerchantProduct(this.getSysUserVO().getName(), merchantProductDTO));
    }



    @ApiOperation(value = "商户分配通道")
    @PostMapping("/allotMerProductChannel")
    public BaseResponse allotMerProductChannel(@RequestBody @ApiParam @Valid MerProDTO merProDTO) {
        return ResultUtil.success(merchantProductService.allotMerProductChannel(this.getSysUserVO().getUsername(), merProDTO));
    }



}
