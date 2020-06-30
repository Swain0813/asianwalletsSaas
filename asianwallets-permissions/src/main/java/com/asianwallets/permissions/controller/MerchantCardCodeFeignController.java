package com.asianwallets.permissions.controller;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.MerchantCardCodeDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.MerchantCardCodeFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "商户码牌管理接口")
@RequestMapping("/cardCode")
public class MerchantCardCodeFeignController extends BaseController {

    @Autowired
    private MerchantCardCodeFeign merchantCardCodeFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "分页查询商户码牌信息信息")
    @PostMapping("/pageFindMerchantCardCode")
    public BaseResponse pageFindMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantCardCodeDTO),
                "分页查询商户码牌信息信息"));
        return merchantCardCodeFeign.pageFindMerchantCardCode(merchantCardCodeDTO);
    }

    @ApiOperation(value = "查询商户码牌详情信息")
    @PostMapping("/getMerchantCardCode")
    public BaseResponse getMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantCardCodeDTO),
                "查询商户码牌详情信息"));
        return merchantCardCodeFeign.getMerchantCardCode(merchantCardCodeDTO);
    }

    @ApiOperation(value = "修改商户码牌信息")
    @PostMapping("/updateMerchantCardCode")
    public BaseResponse updateMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO){
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(merchantCardCodeDTO),
                "修改商户码牌信息"));
        return merchantCardCodeFeign.updateMerchantCardCode(merchantCardCodeDTO);
    }

    @ApiOperation(value = "查看商户静态码")
    @PostMapping("/selectMerchantCardCode")
    public BaseResponse selectMerchantCardCode(@RequestBody @ApiParam MerchantCardCodeDTO merchantCardCodeDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(merchantCardCodeDTO),
                "查看商户静态码"));
        return merchantCardCodeFeign.selectMerchantCardCode(merchantCardCodeDTO);
    }
}
