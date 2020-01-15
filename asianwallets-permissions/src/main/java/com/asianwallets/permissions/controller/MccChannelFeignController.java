package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.MccChannelDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.MccChannelExportVO;
import com.asianwallets.permissions.feign.base.MccChannelFeign;
import com.asianwallets.permissions.service.MccChannelFeignService;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName mcc
 * @Description mcc
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/mccChannel")
@Api(description = "mcc映射表接口")
@Slf4j
public class MccChannelFeignController extends BaseController {

    @Value("${file.tmpfile}")
    private String tmpfile;

    @Autowired
    private MccChannelFeign mccChannelFeign;

    @Autowired
    private MccChannelFeignService mccChannelFeignService;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "新增mccChannel")
    @PostMapping("addMccChannel")
    public BaseResponse addMccChannel(@RequestBody @ApiParam MccChannelDTO mc) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(),
                AsianWalletConstant.ADD, JSON.toJSONString(mc),
                "新增mccChannel"));
        return mccChannelFeign.addMccChannel(mc);
    }

    @ApiOperation(value = "查询mccChannel")
    @PostMapping("pageMccChannel")
    public BaseResponse pageMccChannel(@RequestBody @ApiParam MccChannelDTO mc) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(),
                AsianWalletConstant.SELECT, JSON.toJSONString(mc),
                "查询mccChannel"));
        return mccChannelFeign.pageMccChannel(mc);
    }

    @ApiOperation(value = "启用禁用mccChannel")
    @PostMapping("banMccChannel")
    public BaseResponse banMccChannel(@RequestBody @ApiParam MccChannelDTO mc) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(),
                AsianWalletConstant.UPDATE, JSON.toJSONString(mc),
                "启用禁用mccChannel"));
        return mccChannelFeign.banMccChannel(mc);
    }

    @ApiOperation(value = "查询所有mccChannel")
    @GetMapping("inquireAllMccChannel")
    public BaseResponse inquireAllMccChannel() {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(),
                AsianWalletConstant.SELECT, null,
                "查询所有mccChannel"));
        return mccChannelFeign.inquireAllMccChannel();
    }


    @ApiOperation(value = "导入mccChannel")
    @PostMapping("importMccChannel")
    public BaseResponse importMccChannel(@RequestParam("file") @ApiParam MultipartFile file) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(),
                AsianWalletConstant.ADD, null,
                "导入mccChannel"));
        return mccChannelFeign.importMccChannel(mccChannelFeignService.uploadMccChannel(file, this.getUserName().getUsername()));
    }

    @ApiOperation(value = "导出mccChannel")
    @PostMapping("exportMccChannel")
    public BaseResponse exportMccChannel(@RequestBody @ApiParam MccChannelDTO mc, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(),
                AsianWalletConstant.SELECT, null,
                "导出mccChannel"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<MccChannelExportVO> lists = mccChannelFeign.exportMccChannel(mc);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(lists)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils excelUtils = new ExcelUtils();
            excelUtils.exportExcel(lists, MccChannelExportVO.class, writer);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出mccChannel】==========【导出mccChannel】", e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "查询所有的通道")
    @GetMapping("selectAllChannel")
    public BaseResponse selectAllChannel() {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(),
                AsianWalletConstant.SELECT, null,
                "查询所有的通道"));
        return ResultUtil.success(mccChannelFeignService.selectAllChannel());
    }
}

