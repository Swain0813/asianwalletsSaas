package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.MccDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.MccExportVO;
import com.asianwallets.common.vo.MccVO;
import com.asianwallets.permissions.feign.base.MccFeign;
import com.asianwallets.permissions.service.MccFeignService;
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
 * @ClassName MCC
 * @Description MCC
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/mcc")
@Api(description = "MCC管理接口")
@Slf4j
public class MccFeignController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private MccFeign mccFeign;

    @Autowired
    private MccFeignService mccFeignService;

    @Value("${file.tmpfile}")
    private String tmpfile;

    @ApiOperation(value = "新增mcc")
    @PostMapping("addMcc")
    public BaseResponse addMcc(@RequestBody @ApiParam MccDTO mccDto) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(),
                AsianWalletConstant.ADD, JSON.toJSONString(mccDto),
                "新增mcc"));
        return mccFeign.addMcc(mccDto);
    }

    @ApiOperation(value = "修改mcc")
    @PostMapping("updateMcc")
    public BaseResponse updateMcc(@RequestBody @ApiParam MccDTO mccDto) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(),
                AsianWalletConstant.UPDATE, JSON.toJSONString(mccDto),
                "修改mcc"));
        return mccFeign.updateMcc(mccDto);
    }

    @ApiOperation(value = "查询mcc")
    @PostMapping("pageMcc")
    public BaseResponse pageMcc(@RequestBody @ApiParam MccDTO mccDto) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(),
                AsianWalletConstant.SELECT, JSON.toJSONString(mccDto),
                "查询mcc"));
        return mccFeign.pageMcc(mccDto);
    }

    @ApiOperation(value = "启用禁用mcc")
    @PostMapping("banMcc")
    public BaseResponse banMcc(@RequestBody @ApiParam MccDTO mccDto) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(),
                AsianWalletConstant.UPDATE, JSON.toJSONString(mccDto),
                "启用禁用mcc"));
        return mccFeign.banMcc(mccDto);
    }

    @ApiOperation(value = "查询所有mcc")
    @GetMapping("inquireAllMcc")
    public BaseResponse inquireAllMcc() {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(),
                AsianWalletConstant.SELECT, null,
                "查询所有mcc"));
        return mccFeign.inquireAllMcc();
    }


    @ApiOperation(value = "导入mcc")
    @PostMapping("importMcc")
    public BaseResponse importMcc(@RequestParam("file") @ApiParam MultipartFile file) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(),
                AsianWalletConstant.ADD, null,
                "导入mcc"));
        return mccFeign.importMcc(mccFeignService.uploadMcc(file, this.getSysUserVO().getUsername()));
    }

    @ApiOperation(value = "导出mcc")
    @PostMapping("exportMcc")
    public BaseResponse exportMcc(@RequestBody @ApiParam MccDTO mccDto, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(),
                AsianWalletConstant.SELECT, null,
                "导出mcc"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<MccVO> lists = mccFeign.exportMcc(mccDto);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(lists)) {
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils excelUtils = new ExcelUtils();
            excelUtils.exportExcel(lists, MccExportVO.class, writer);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出mcc】==========【导出mcc失败】", e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }
}
