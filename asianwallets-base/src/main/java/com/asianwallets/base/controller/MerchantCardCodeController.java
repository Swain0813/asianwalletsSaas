package com.asianwallets.base.controller;
import com.asianwallets.base.service.MerchantCardCodeService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.MerchantCardCodeDTO;
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

@RestController
@RequestMapping("/cardCode")
@Api(description = "码牌管理接口")
public class MerchantCardCodeController extends BaseController {

    @Autowired
    private MerchantCardCodeService merchantCardCodeService;

    @ApiOperation(value = "分页查询商户码牌信息信息")
    @PostMapping("/pageFindMerchantCardCode")
    public BaseResponse pageFindMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO) {
        return ResultUtil.success(merchantCardCodeService.pageFindMerchantCardCode(merchantCardCodeDTO));
    }

    @ApiOperation(value = "查询商户码牌详情信息")
    @PostMapping("/getMerchantCardCode")
    public BaseResponse getMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO) {
        return ResultUtil.success(merchantCardCodeService.getMerchantCardCode(merchantCardCodeDTO));
    }

    @ApiOperation(value = "修改商户码牌信息")
    @PostMapping("/updateMerchantCardCode")
    public BaseResponse updateMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO){
        return ResultUtil.success(merchantCardCodeService.updateMerchantCardCode(this.getSysUserVO().getUsername(),merchantCardCodeDTO));
    }

    @ApiOperation(value = "查看商户静态码")
    @PostMapping("/selectMerchantCardCode")
    public BaseResponse selectMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO) {
        return ResultUtil.success(merchantCardCodeService.selectMerchantCardCode(merchantCardCodeDTO));
    }
}
