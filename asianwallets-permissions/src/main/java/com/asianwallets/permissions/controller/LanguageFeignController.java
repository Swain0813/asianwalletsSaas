package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.LanguageDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.LanguageFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenxinran
 * @Date: 2019/1/29 16:01
 * @Description: 语种管理接口
 */
@RestController
@Api(description = "语种管理")
@RequestMapping("/language")
public class LanguageFeignController extends BaseController {

    @Autowired
    private LanguageFeign languageFeign;
    /**
     * 操作日志模块
     */
    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "添加语种信息")
    @PostMapping("/addLanguage")
    public BaseResponse addLanguage(@RequestBody @ApiParam LanguageDTO languageDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.ADD, JSON.toJSONString(languageDTO),
                "添加语种信息"));
        return languageFeign.addLanguage(languageDTO);
    }

    @ApiOperation(value = "修改语种信息")
    @PostMapping("/updateLanguage")
    public BaseResponse updateLanguage(@RequestBody @ApiParam LanguageDTO languageDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(languageDTO),
                "修改语种信息"));
        return languageFeign.updateLanguage(languageDTO);
    }

    @ApiOperation(value = "分页查询语种信息")
    @PostMapping("/pageFindLanguage")
    public BaseResponse pageFindLanguage(@RequestBody @ApiParam LanguageDTO languageDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(languageDTO),
                "分页查询语种信息"));
        return languageFeign.pageFindLanguage(languageDTO);
    }

    @ApiOperation(value = "根据id查询语种信息")
    @PostMapping("/getLanguageInfo")
    public BaseResponse getLanguageInfo(@RequestBody @ApiParam LanguageDTO languageDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(languageDTO),
                "根据id查询语种信息"));
        return languageFeign.getLanguageInfo(languageDTO);
    }

    @ApiOperation(value = "启用禁用语种")
    @PostMapping("/banLanguage")
    public BaseResponse banLanguage(@RequestBody @ApiParam LanguageDTO languageDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(languageDTO),
                "启用禁用语种"));
        return languageFeign.banLanguage(languageDTO);
    }
}
