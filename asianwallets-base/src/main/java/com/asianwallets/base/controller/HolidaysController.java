package com.asianwallets.base.controller;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.HolidaysDTO;
import com.asianwallets.common.entity.Holidays;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.base.service.HolidaysService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 节假日接口
 */
@RestController
@Api(description = "节假日接口")
@RequestMapping("/holidays")
public class HolidaysController extends BaseController {

    @Autowired
    private HolidaysService holidaysService;

    @ApiOperation(value = "分页多条件查询节假日信息")
    @PostMapping("getByMultipleConditions")
    public BaseResponse getByMultipleConditions(@RequestBody @ApiParam HolidaysDTO holidaysDTO) {
        return ResultUtil.success(holidaysService.getByMultipleConditions(holidaysDTO));
    }

    @ApiOperation(value = "添加节假日信息")
    @PostMapping("addHolidays")
    public BaseResponse addHolidays(@RequestBody @ApiParam HolidaysDTO holidaysDTO) {
        return ResultUtil.success(holidaysService.addHolidays(holidaysDTO, this.getUserName()));
    }

    @ApiOperation(value = "禁用节假日信息")
    @PostMapping("banHolidays")
    public BaseResponse updateHolidays(@RequestBody @ApiParam HolidaysDTO holidaysDTO) {
        return ResultUtil.success(holidaysService.banHolidays(holidaysDTO, this.getUserName()));
    }

    @ApiOperation(value = "导入节假日信息")
    @PostMapping("uploadFiles")
    public BaseResponse uploadFiles(@RequestBody @ApiParam List<Holidays> fileList) {
        return ResultUtil.success(holidaysService.uploadFiles(fileList));
    }

}


