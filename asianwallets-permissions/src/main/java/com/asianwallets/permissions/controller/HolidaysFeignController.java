package com.asianwallets.permissions.controller;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.HolidaysDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.HolidaysFeign;
import com.asianwallets.permissions.service.HolidayFeignService;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: XuWenQi
 * @create: 2019-01-30 16:31
 **/
@RestController
@Api(description = "节假日管理接口")
@RequestMapping("/holidays")
public class HolidaysFeignController extends BaseController {
    @Autowired
    private HolidayFeignService holidayFeignService;

    @Autowired
    private HolidaysFeign holidaysFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Value("${file.tmpfile}")
    private String tmpfile;//springboot启动的临时文件存放

    @ApiOperation(value = "添加节假日信息")
    @PostMapping("addHolidays")
    public BaseResponse addHolidays(@RequestBody @ApiParam HolidaysDTO holidaysDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.ADD, JSON.toJSONString(holidaysDTO),
                "添加节假日信息"));
        return holidaysFeign.addHolidays(holidaysDTO);
    }


    @ApiOperation(value = "禁用节假日信息")
    @PostMapping("banHolidays")
    public BaseResponse updateHolidays(@RequestBody @ApiParam HolidaysDTO holidaysDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(holidaysDTO),
                "禁用节假日信息"));
        return holidaysFeign.banHolidays(holidaysDTO);
    }

    @ApiOperation(value = "分页多条件查询节假日信息")
    @PostMapping("getByMultipleConditions")
    public BaseResponse getByMultipleConditions(@RequestBody @ApiParam HolidaysDTO holidaysDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(holidaysDTO),
                "分页多条件查询节假日信息"));
        return holidaysFeign.getByMultipleConditions(holidaysDTO);
    }


    @ApiOperation(value = "导入节假日信息")
    @PostMapping("uploadFiles")
    public BaseResponse uploadFiles(@RequestParam("file") @ApiParam MultipartFile file) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.ADD, null,
                "导入节假日信息"));
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(tmpfile);//指定临时文件路径，这个路径可以随便写
        factory.createMultipartConfig();
        return holidaysFeign.uploadFiles(holidayFeignService.uploadFiles(file, this.getUserName()));
    }


}


