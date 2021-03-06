package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.MerchantCardCodeDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.MerchantCardCodeFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "asianwallets-base", fallback = MerchantCardCodeFeignImpl.class)
public interface MerchantCardCodeFeign {

    @ApiOperation(value = "分页查询商户码牌信息信息")
    @PostMapping("/cardCode/pageFindMerchantCardCode")
    BaseResponse pageFindMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO);

    @ApiOperation(value = "查询商户码牌详情信息")
    @PostMapping("/cardCode/getMerchantCardCode")
    BaseResponse getMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO);


    @ApiOperation(value = "修改商户码牌信息")
    @PostMapping("/cardCode/updateMerchantCardCode")
    BaseResponse updateMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO);

    @ApiOperation(value = "查看商户静态码")
    @PostMapping("/cardCode/selectMerchantCardCode")
    BaseResponse selectMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO);
}
