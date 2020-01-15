package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.DictionaryInfoAllDTO;
import com.asianwallets.common.dto.DictionaryInfoDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.DictionaryInfoFeign;
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
 * @Date: 2019/2/1 15:56
 * @Description: 字典类型与数据操作Controller
 */
@RestController
@Api(description = "字典类型与数据操作管理")
@RequestMapping("/dictionaryinfo")
public class DictionaryInfoFeignController extends BaseController {

    @Autowired
    private DictionaryInfoFeign dictionaryInfoFeign;

    /**
     * 操作日志模块
     */
    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "添加字典信息")
    @PostMapping("/addDictionaryInfo")
    public BaseResponse addDictionaryInfo(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.ADD, JSON.toJSONString(dictionaryInfoDTO),
                "添加字典信息"));
        return dictionaryInfoFeign.addDictionaryInfo(dictionaryInfoDTO);
    }

    @ApiOperation(value = "启用禁用字典类型")
    @PostMapping("/banDictionaryType")
    public BaseResponse banDictionaryType(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(dictionaryInfoDTO),
                "启用禁用字典类型"));
        return dictionaryInfoFeign.banDictionaryType(dictionaryInfoDTO);
    }


    @ApiOperation(value = "新增语言")
    @PostMapping("/addOtherLanguage")
    public BaseResponse addOtherLanguage(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {

        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.ADD, JSON.toJSONString(dictionaryInfoDTO),
                "新增语言"));
        return dictionaryInfoFeign.addOtherLanguage(dictionaryInfoDTO);
    }

    @ApiOperation(value = "启用禁用字典数据")
    @PostMapping("/banDictionary")
    public BaseResponse banDictionary(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(dictionaryInfoDTO),
                "启用禁用字典类型"));
        return dictionaryInfoFeign.banDictionary(dictionaryInfoDTO);
    }

    @ApiOperation(value = "修改字典类型索引")
    @PostMapping("/updateDictionaryType")
    public BaseResponse updateDictionaryType(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(dictionaryInfoDTO),
                "修改字典类型索引"));
        return dictionaryInfoFeign.updateDictionaryType(dictionaryInfoDTO);
    }

    @ApiOperation(value = "修改字典数据")
    @PostMapping("/updateDictionary")
    public BaseResponse updateDictionary(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(dictionaryInfoDTO),
                "修改字典数据"));
        return dictionaryInfoFeign.updateDictionary(dictionaryInfoDTO);
    }

    @ApiOperation(value = "根据id查询字典数据")
    @PostMapping("/getDictionaryInfo")
    public BaseResponse getDictionaryInfo(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(dictionaryInfoDTO),
                "根据id查询字典数据"));
        return dictionaryInfoFeign.getDictionaryInfo(dictionaryInfoDTO);
    }

    @ApiOperation(value = "分页查询字典类型信息")
    @PostMapping("/pageDicTypeInfo")
    public BaseResponse pageDicTypeInfo(@RequestBody @ApiParam DictionaryInfoDTO dictionaryInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(dictionaryInfoDTO),
                "分页查询字典类型信息"));
        return dictionaryInfoFeign.pageDicTypeInfo(dictionaryInfoDTO);
    }

    @ApiOperation(value = "查询全部数据字典信息")
    @PostMapping("/pageDictionaryInfos")
    public BaseResponse pageDictionaryInfos(@RequestBody @ApiParam DictionaryInfoAllDTO dictionaryInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(dictionaryInfoDTO),
                "查询全部数据字典信息"));
        return dictionaryInfoFeign.pageDictionaryInfos(dictionaryInfoDTO);
    }
}
