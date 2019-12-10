package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.MccChannelDTO;
import com.asianwallets.common.entity.MccChannel;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.MccChannelExportVO;
import com.asianwallets.permissions.feign.base.impl.MccChannelFeignImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 国家模块Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = MccChannelFeignImpl.class)
public interface MccChannelFeign {

    @PostMapping("/mccChannel/addMccChannel")
    BaseResponse addMccChannel(@RequestBody @ApiParam MccChannelDTO mc);

    @PostMapping("/mccChannel/pageMccChannel")
    BaseResponse pageMccChannel(@RequestBody @ApiParam MccChannelDTO mc);

    @PostMapping("/mccChannel/banMccChannel")
    BaseResponse banMccChannel(@RequestBody @ApiParam MccChannelDTO mc);

    @GetMapping("/mccChannel/inquireAllMccChannel")
    BaseResponse inquireAllMccChannel();

    @PostMapping("/mccChannel/importMccChannel")
    BaseResponse importMccChannel(@RequestBody @ApiParam List<MccChannel> list);

    @PostMapping("/mccChannel/exportMccChannel")
    List<MccChannelExportVO> exportMccChannel(@RequestBody @ApiParam MccChannelDTO mc);
}
