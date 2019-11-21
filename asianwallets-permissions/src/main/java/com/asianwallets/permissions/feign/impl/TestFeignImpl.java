package com.asianwallets.permissions.feign.impl;

import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.TestFeign;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-21 15:34
 **/
@Component
public class TestFeignImpl implements TestFeign {
    @Override
    public BaseResponse test() {
        return ResultUtil.error("error","error");
    }
}
