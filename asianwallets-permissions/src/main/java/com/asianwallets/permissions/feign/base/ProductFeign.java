package com.asianwallets.permissions.feign.base;


import com.asianwallets.common.dto.ProductDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.ProductFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "asianwallets-base", fallback = ProductFeignImpl.class)
public interface ProductFeign {

    @ApiOperation(value = "添加产品")
    @PostMapping("/product/addProduct")
    BaseResponse addProduct(@RequestBody @ApiParam ProductDTO productDTO);

    @ApiOperation(value = "更新产品")
    @PostMapping("/product/updateProduct")
    BaseResponse updateProduct(@RequestBody @ApiParam ProductDTO productDTO);

    @ApiOperation(value = "分页查询产品")
    @PostMapping("/product/pageProduct")
    BaseResponse pageProduct(@RequestBody @ApiParam ProductDTO productDTO);

    @ApiOperation(value = "查询产品")
    @PostMapping("/product/selectProduct")
    BaseResponse selectProduct(@RequestBody @ApiParam ProductDTO productDTO);

}
