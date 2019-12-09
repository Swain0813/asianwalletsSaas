package com.asianwallets.base.controller;

import com.asianwallets.base.service.MccChannelService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.MccChannelDTO;
import com.asianwallets.common.entity.MccChannel;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.MccChannelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@Api("mcc映射")
public class MccChannelController extends BaseController {

    @Autowired
    private MccChannelService mccChannelService;

    @ApiOperation(value = "新增mccChannel")
    @PostMapping("addMccChannel")
    public BaseResponse addMccChannel(@RequestBody @ApiParam MccChannelDTO mc) {
        mc.setCreator(this.getSysUserVO().getUsername());
        mc.setLanguage(this.getLanguage());
        return ResultUtil.success(mccChannelService.addMccChannel(mc));
    }

    @ApiOperation(value = "查询mccChannel")
    @PostMapping("pageMccChannel")
    public BaseResponse pageMccChannel(@RequestBody @ApiParam MccChannelDTO mc) {
        return ResultUtil.success(mccChannelService.pageMccChannel(mc));
    }

    @ApiOperation(value = "启用禁用mccChannel")
    @PostMapping("banMccChannel")
    public BaseResponse banMccChannel(@RequestBody @ApiParam MccChannelDTO mc) {
        mc.setModifier(this.getSysUserVO().getUsername());
        mc.setLanguage(this.getLanguage());
        return ResultUtil.success(mccChannelService.banMccChannel(mc));
    }

    @ApiOperation(value = "查询所有mccChannel")
    @GetMapping("inquireAllMccChannel")
    public BaseResponse inquireAllMccChannel() {
        return ResultUtil.success(mccChannelService.inquireAllMccChannel(null));
    }


    @ApiOperation(value = "导入mccChannel")
    @PostMapping("importMccChannel")
    public BaseResponse importMccChannel(@RequestBody @ApiParam List<MccChannel> list) {
        return ResultUtil.success(mccChannelService.importMccChannel(list));
    }

    @ApiOperation(value = "导出mccChannel")
    @PostMapping("exportMccChannel")
    public List<MccChannelVO> exportMcc(@RequestBody @ApiParam MccChannelDTO mc) {
        mc.setLanguage(this.getLanguage());
        return mccChannelService.exportMccChannel(mc);
    }
}
