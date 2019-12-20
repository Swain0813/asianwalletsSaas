package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.Ad3Service;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.ChannelsRequestDTO;
import com.asianwallets.common.dto.ad3.AD3CSBScanPayDTO;
import com.asianwallets.common.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "AD3")
@RequestMapping("/ad3")
public class Ad3Controller extends BaseController {

    @Autowired
    private Ad3Service ad3Service;

    @ApiOperation(value = "AD3线下CSB接口")
    @PostMapping("offlineCsb")
    public BaseResponse offlineCsb(@RequestBody @ApiParam AD3CSBScanPayDTO ad3CSBScanPayDTO, @RequestBody @ApiParam ChannelsRequestDTO channelsRequestDTO) {
        return ad3Service.offlineCsb(ad3CSBScanPayDTO, channelsRequestDTO);
    }
}
