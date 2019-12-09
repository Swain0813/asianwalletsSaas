package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.MccDTO;
import com.asianwallets.common.entity.Mcc;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.MccVO;
import com.asianwallets.permissions.feign.base.impl.MccFeignImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 国家模块Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = MccFeignImpl.class)
public interface MccFeign {

    @PostMapping("/mcc/addMcc")
    BaseResponse addMcc(@RequestBody @ApiParam MccDTO mccDto);

    @PostMapping("/mcc/updateMcc")
    BaseResponse updateMcc(@RequestBody @ApiParam MccDTO mccDto);

    @PostMapping("/mcc/pageMcc")
    BaseResponse pageMcc(@RequestBody @ApiParam MccDTO mccDto);

    @PostMapping("/mcc/banMcc")
    BaseResponse banMcc(@RequestBody @ApiParam MccDTO mccDto);

    @GetMapping("/mcc/inquireAllMcc")
    BaseResponse inquireAllMcc();

    @PostMapping("/mcc/importMcc")
    BaseResponse importMcc(@RequestBody @ApiParam List<Mcc> list);

    @PostMapping("/mcc/exportMcc")
    List<MccVO> exportMcc(@RequestBody @ApiParam MccDTO mccDto);
}
