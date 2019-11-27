package com.asianwallets.base.controller;

import com.asianwallets.base.service.DictionaryInfoService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.DictionaryInfoAllDTO;
import com.asianwallets.common.dto.DictionaryInfoDTO;
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
 * @Date: 2019/1/30 14:14
 * @Description: 字典类型与数据管理接口
 */
@RestController
@Api(description = "字典类型与数据管理接口")
@RequestMapping("/dictionaryinfo")
public class DictionaryInfoController extends BaseController {

    @Autowired
    private DictionaryInfoService dictionaryInfoService;

    @ApiOperation(value = "添加信息")
    @PostMapping("/addDictionaryInfo")
    public BaseResponse addDictionaryInfo(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        if (dictionaryInfoDTO.getLanguage() == null) {
            dictionaryInfoDTO.setLanguage(this.getLanguage());
        }
        dictionaryInfoDTO.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(dictionaryInfoService.addDictionaryInfo(dictionaryInfoDTO));
    }

    @ApiOperation(value = "启用禁用字典类型")
    @PostMapping("/banDictionaryType")
    public BaseResponse banDictionaryType(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        return ResultUtil.success(dictionaryInfoService.banDictionaryType(this.getSysUserVO().getUsername(), dictionaryInfoDTO.getDictypeCode(), dictionaryInfoDTO.getEnabled()));
    }

    @ApiOperation(value = "启用禁用字典数据")
    @PostMapping("/banDictionary")
    public BaseResponse banDictionary(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        return ResultUtil.success(dictionaryInfoService.banDictionary(this.getSysUserVO().getUsername(), dictionaryInfoDTO.getId(), dictionaryInfoDTO.getEnabled()));
    }

    @ApiOperation(value = "修改字典类型索引")
    @PostMapping("/updateDictionaryType")
    public BaseResponse updateDictionaryType(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        dictionaryInfoDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(dictionaryInfoService.updateDictionaryType(dictionaryInfoDTO));
    }

    @ApiOperation(value = "修改字典数据")
    @PostMapping("/updateDictionary")
    public BaseResponse updateDictionary(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        dictionaryInfoDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(dictionaryInfoService.updateDictionary(dictionaryInfoDTO));
    }

    @ApiOperation(value = "根据id查询数据")
    @PostMapping("/getDictionaryInfo")
    public BaseResponse getDictionaryInfo(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        return ResultUtil.success(dictionaryInfoService.getDictionaryInfo(dictionaryInfoDTO.getId()));

    }

    @ApiOperation(value = "新增语言")
    @PostMapping("/addOtherLanguage")
    public BaseResponse addOtherLanguage(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        dictionaryInfoDTO.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(dictionaryInfoService.addOtherLanguage(dictionaryInfoDTO));
    }

    @ApiOperation(value = "分页查询类型信息")
    @PostMapping("/pageDicTypeInfo")
    public BaseResponse pageDicTypeInfo(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        return ResultUtil.success(dictionaryInfoService.pageDicTypeInfo(dictionaryInfoDTO));
    }

    @ApiOperation(value = "查询全部数据字典信息")
    @PostMapping("/pageDictionaryInfos")
    public BaseResponse pageDictionaryInfos(@RequestBody @ApiParam DictionaryInfoAllDTO dictionaryInfoDTO) {
        return ResultUtil.success(dictionaryInfoService.pageDictionaryInfos(dictionaryInfoDTO));
    }
}
