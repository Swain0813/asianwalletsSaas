package com.asianwallets.permissions.feign;

import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.impl.TestFeignImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "asianwallets-baseinfo", fallback = TestFeignImpl.class)
public interface TestFeign {

    @GetMapping("/base/test")
    BaseResponse test();

}
