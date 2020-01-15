package com.asianwallets.base.controller;
import com.asianwallets.base.service.AttestationService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.AttestationDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attestation")
@Api(description = "密钥管理")
public class AttestationController extends BaseController {

    @Autowired
    private AttestationService attestationService;

    @ApiOperation(value = "生成RSA公私钥")
    @GetMapping("/getRSA")
    public BaseResponse getRSA() {
        return ResultUtil.success(attestationService.getRSA());
    }

    @ApiOperation(value = "查询公钥")
    @PostMapping("/pageKeyInfo")
    public BaseResponse pageKeyInfo(@RequestBody @ApiParam AttestationDTO attestationDTO) {
        return ResultUtil.success(attestationService.selectKeyInfo(attestationDTO));
    }

    @ApiOperation(value = "修改密钥信息")
    @PostMapping("/updateKeyInfo")
    public BaseResponse updateKeyInfo(@RequestBody @ApiParam AttestationDTO attestationDTO) {
        attestationDTO.setModifier(this.getUserName().getUsername());
        return ResultUtil.success(attestationService.updateKeyInfo(attestationDTO));
    }

}
