package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.HolidaysDTO;
import com.asianwallets.common.entity.Holidays;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.HolidaysFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 节假日模块Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = HolidaysFeignImpl.class)
public interface HolidaysFeign {

    @ApiOperation(value = "添加节假日信息")
    @PostMapping("/holidays/addHolidays")
    BaseResponse addHolidays(@RequestBody @ApiParam HolidaysDTO holidaysDTO);

    @ApiOperation(value = "禁用节假日信息")
    @PostMapping("/holidays/banHolidays")
    BaseResponse banHolidays(@RequestBody @ApiParam HolidaysDTO holidaysDTO);

    @ApiOperation(value = "分页多条件查询节假日信息")
    @PostMapping("/holidays/getByMultipleConditions")
    BaseResponse getByMultipleConditions(@RequestBody @ApiParam HolidaysDTO holidaysDTO);

    @ApiOperation(value = "导入节假日信息")
    @PostMapping(value = "/holidays/uploadFiles")
    BaseResponse uploadFiles(@RequestBody @ApiParam List<Holidays> list);
}
