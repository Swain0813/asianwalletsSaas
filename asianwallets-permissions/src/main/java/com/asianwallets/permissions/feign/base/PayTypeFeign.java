package com.asianwallets.permissions.feign.base;


import com.asianwallets.common.dto.PayTypeDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.PayTypeFeignImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "asianwallets-base", fallback = PayTypeFeignImpl.class)
public interface PayTypeFeign {
    @PostMapping("/paytype/addPayType")
    BaseResponse addPaytype(@RequestBody @ApiParam PayTypeDTO PayTypeDTO);

    @PostMapping("/paytype/updatePayType")
    BaseResponse updatePaytype(@RequestBody @ApiParam PayTypeDTO PayTypeDTO);

    @PostMapping("/paytype/pagePayType")
    BaseResponse pagePaytype(@RequestBody @ApiParam PayTypeDTO PayTypeDTO);

    @PostMapping("/paytype/banPayType")
    BaseResponse banCurrency(@RequestBody @ApiParam PayTypeDTO PayTypeDTO);

   /* @GetMapping("/paytype/inquireAllPayType")
    BaseResponse inquireAllPaytype();*/
}
