package com.asianwallets.base.controller;
import com.asianwallets.base.service.ChargesTypeService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.ChargesTypeDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenxinran
 * @Date: 2019/1/25 11:15
 * @Description: 费率管理接口
 */
@Api(description ="费率管理接口")
@RestController
@RequestMapping("/chargestype")
public class ChargesTypeController extends BaseController {

    @Autowired
    private ChargesTypeService chargesTypeService;

    @ApiOperation(value = "分页查询费率")
    @PostMapping("/pageChargesCondition")
    public BaseResponse pageChargesCondition(@RequestBody @ApiParam ChargesTypeDTO chargesTypeDTO) {
        if (StringUtils.isBlank(chargesTypeDTO.getLanguage())) {
            chargesTypeDTO.setLanguage(this.getLanguage());
        }
        return ResultUtil.success(chargesTypeService.pageChargesCondition(chargesTypeDTO));
    }

    @ApiOperation(value = "根据ID查询费率")
    @PostMapping("/getChargesInfo")
    public BaseResponse getChargesInfo(@RequestBody ChargesTypeDTO chargesTypeDTO) {
        return ResultUtil.success(chargesTypeService.getChargesInfo(chargesTypeDTO.getId()));
    }

    @ApiOperation(value = "新增费率")
    @PostMapping("/addChargesType")
    public BaseResponse addChargesType(@RequestBody @ApiParam ChargesTypeDTO chargesTypeDTO) {
        chargesTypeDTO.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(chargesTypeService.addChargesType(chargesTypeDTO));
    }

    @ApiOperation(value = "修改费率")
    @PostMapping("/updateChargesType")
    public BaseResponse updateChargesType(@RequestBody @ApiParam ChargesTypeDTO chargesTypeDTO) {
        chargesTypeDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(chargesTypeService.updateChargesType(chargesTypeDTO));
    }

    @ApiOperation(value = "启用禁用费率")
    @PostMapping("/banChargesType")
    public BaseResponse banChargesType(@RequestBody @ApiParam ChargesTypeDTO chargesTypeDTO) {
        return ResultUtil.success(chargesTypeService.banChargesType(chargesTypeDTO.getEnabled(), chargesTypeDTO.getId(), this.getSysUserVO().getUsername()));
    }

}