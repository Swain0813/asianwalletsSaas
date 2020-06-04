package com.asianwallets.permissions.controller;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.SearchAccountCheckDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.CheckAccountAuditVO;
import com.asianwallets.common.vo.CheckAccountVO;
import com.asianwallets.permissions.feign.base.AccountCheckFeign;
import com.asianwallets.permissions.service.ExportService;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
@Api(description = "通道对账接口")
@RequestMapping("/finance")
public class AccountCheckFeignController extends BaseController {

    @Autowired
    private AccountCheckFeign accountCheckFeign;

    @Autowired
    private OperationLogService operationLogService;

    @Value("${file.tmpfile}")
    private String tmpfile;//springboot启动的临时文件存放

    @Autowired
    private ExportService exportService;

    @ApiOperation(value = "分页查询对账管理")
    @PostMapping("/pageAccountCheckLog")
    public BaseResponse pageAccountCheckLog(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(searchAccountCheckDTO),
                "分页查询对账管理"));
        return accountCheckFeign.pageAccountCheckLog(searchAccountCheckDTO);
    }

    @ApiOperation(value = "导入通道对账单")
    @PostMapping("/channelAccountCheck")
    public BaseResponse channelAccountCheck(@RequestParam("file") @ApiParam MultipartFile file) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, null,
                "导入通道对账单"));
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(tmpfile);//指定临时文件路径，这个路径可以随便写
        factory.createMultipartConfig();
        return accountCheckFeign.channelAccountCheck(file);
    }

    @ApiOperation(value = "分页查询对账管理详情")
    @PostMapping("/pageAccountCheck")
    public BaseResponse pageAccountCheck(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(searchAccountCheckDTO),
                "分页查询对账管理详情"));
        return accountCheckFeign.pageAccountCheck(searchAccountCheckDTO);
    }

    /**
     * 差错处理一览 以及 对账管理详情用
     * @param searchAccountCheckDTO
     * @return
     */
    @ApiOperation(value = "导出对账管理详情")
    @PostMapping("/exportAccountCheck")
    public BaseResponse exportAccountCheck(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(searchAccountCheckDTO),
                "导出对账管理详情"));
        BaseResponse baseResponse = accountCheckFeign.exportAccountCheck(searchAccountCheckDTO);
        ArrayList<LinkedHashMap> data = (ArrayList<LinkedHashMap>) baseResponse.getData();
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            ServletOutputStream out = response.getOutputStream();
            if (data == null || data.size() == 0) {//数据不存在的场合
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ArrayList<CheckAccountVO> checkAccountVOS = new ArrayList<>();
            for (LinkedHashMap datum : data) {
                checkAccountVOS.add(JSON.parseObject(JSON.toJSONString(datum), CheckAccountVO.class));
            }
            writer = exportService.getCheckAccountWriter(checkAccountVOS, CheckAccountVO.class);
            writer.flush(out);
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "差错处理和补单")
    @GetMapping("/updateCheckAccount")
    public BaseResponse updateCheckAccount(@RequestParam @ApiParam String checkAccountId
            , @RequestParam(required = false) @ApiParam String remark) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(checkAccountId),
                "差错处理和补单"));
        return accountCheckFeign.updateCheckAccount(checkAccountId, remark);
    }

    /**
     * 差错复核一览
     * @param searchAccountCheckDTO
     * @return
     */
    @ApiOperation(value = "分页查询对账管理复核详情")
    @PostMapping("/pageAccountCheckAudit")
    public BaseResponse pageAccountCheckAudit(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(searchAccountCheckDTO),
                "分页查询对账管理复核详情"));
        return accountCheckFeign.pageAccountCheckAudit(searchAccountCheckDTO);
    }

    /**
     * 差错复核导出
     * @param searchAccountCheckDTO
     * @return
     */
    @ApiOperation(value = "导出对账管理复核详情")
    @PostMapping("/exportAccountCheckAudit")
    public BaseResponse exportAccountCheckAudit(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO,HttpServletResponse response) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(searchAccountCheckDTO),
                "导出对账管理复核详情"));
        BaseResponse baseResponse = accountCheckFeign.exportAccountCheckAudit(searchAccountCheckDTO);
        ArrayList<LinkedHashMap> data = (ArrayList<LinkedHashMap>) baseResponse.getData();
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            ServletOutputStream out = response.getOutputStream();
            if (data == null || data.size() == 0) {//数据不存在的场合
                //数据不存在的场合
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ArrayList<CheckAccountAuditVO> checkAccountVOS = new ArrayList<>();
            for (LinkedHashMap datum : data) {
                checkAccountVOS.add(JSON.parseObject(JSON.toJSONString(datum), CheckAccountAuditVO.class));
            }
            writer = exportService.getCheckAccountAuditWriter(checkAccountVOS, CheckAccountAuditVO.class);
            writer.flush(out);
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "差错复核")
    @GetMapping("/auditCheckAccount")
    public BaseResponse auditCheckAccount(@RequestParam @ApiParam String checkAccountId
            , @RequestParam @ApiParam Boolean enable, @RequestParam(required = false) @ApiParam String remark) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(checkAccountId),
                "差错复核"));
        return accountCheckFeign.auditCheckAccount(checkAccountId, enable, remark);
    }
}
