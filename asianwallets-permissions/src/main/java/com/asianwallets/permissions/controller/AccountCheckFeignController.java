package com.asianwallets.permissions.controller;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.SearchAccountCheckDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.AccountCheckFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public BaseResponse exportAccountCheck(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(searchAccountCheckDTO),
                "导出对账管理详情"));
        return accountCheckFeign.exportAccountCheck(searchAccountCheckDTO);
    }

    @ApiOperation(value = "差错处理")
    @GetMapping("/updateCheckAccount")
    public BaseResponse updateCheckAccount(@RequestParam @ApiParam String checkAccountId
            , @RequestParam(required = false) @ApiParam String remark) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(checkAccountId),
                "差错处理"));
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
    public BaseResponse exportAccountCheckAudit(@RequestBody @ApiParam SearchAccountCheckDTO searchAccountCheckDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(searchAccountCheckDTO),
                "导出对账管理复核详情"));
        return accountCheckFeign.exportAccountCheckAudit(searchAccountCheckDTO);
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
