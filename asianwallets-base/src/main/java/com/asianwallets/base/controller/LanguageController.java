package com.asianwallets.base.controller;

import com.asianwallets.base.service.LanguageService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.LanguageDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
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
 * @Date: 2019/1/29 14:21
 * @Description: 语种管理 controller
 */
@RestController
@Api(description = "语种管理")
@RequestMapping("/language")
public class LanguageController extends BaseController {

    @Autowired
    private LanguageService languageService;

    @ApiOperation(value = "添加语种")
    @PostMapping("/addLanguage")
    public BaseResponse addLanguage(@RequestBody @ApiParam LanguageDTO languageDTO) {
        languageDTO.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(languageService.addLanguage(languageDTO));
    }

    @ApiOperation(value = "修改语种")
    @PostMapping("/updateLanguage")
    public BaseResponse updateLanguage(@RequestBody @ApiParam LanguageDTO languageDTO) {
        languageDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(languageService.updateLanguage(languageDTO));
    }

    @ApiOperation(value = "分页查询语种信息")
    @PostMapping("/pageFindLanguage")
    public BaseResponse pageFindLanguage(@RequestBody @ApiParam LanguageDTO languageDTO) {
        return ResultUtil.success(languageService.pageFindLanguage(languageDTO));
    }

    @ApiOperation(value = "根据id查询语种信息")
    @PostMapping("/getLanguageInfo")
    public BaseResponse getLanguageInfo(@RequestBody @ApiParam LanguageDTO languageDTO) {
        return ResultUtil.success(languageService.getLanguageInfo(languageDTO.getId()));
    }

    @ApiOperation(value = "启用禁用语种")
    @PostMapping("/banLanguage")
    public BaseResponse banLanguage(@RequestBody @ApiParam LanguageDTO languageDTO) {
        return ResultUtil.success(languageService.banLanguage(this.getSysUserVO().getUsername(), languageDTO.getId(), languageDTO.getEnabled()));
    }
}
