package com.asianwallets.permissions.feign.rights;
import com.asianwallets.common.dto.OtaChannelDTO;
import com.asianwallets.common.entity.OtaChannel;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.rights.impl.OtaChannelFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(value = "asianwallets-rights", fallback = OtaChannelFeignImpl.class)
public interface OtaChannelFeign {

    @ApiOperation(value = "OTA平台分页查询")
    @PostMapping("/otaChannel/pageOtaChannel")
    BaseResponse pageOtaChannel(@RequestBody @ApiParam OtaChannelDTO otaChannelDTO);

    @ApiOperation(value = "添加修改OTA平台")
    @PostMapping("/otaChannel/addOtaChannel")
    BaseResponse addOtaChannel(@RequestBody @ApiParam OtaChannelDTO otaChannelDTO);

    @ApiOperation(value = "发放平台的下来框")
    @PostMapping("/otaChannel/getOtaChannels")
    List<OtaChannel> getOtaChannels();
}
