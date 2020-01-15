package com.asianwallets.base.controller;
import com.asianwallets.base.service.BankIssuerIdService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.BankIssuerIdDTO;
import com.asianwallets.common.entity.BankIssuerId;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.BankIssuerIdVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@Api(description = "银行机构代码映射接口")
@RequestMapping("/bankIssuerId")
public class BankIssuerIdController extends BaseController {

    @Autowired
    private BankIssuerIdService bankIssuerIdService;

    @ApiOperation(value = "添加银行机构代码映射信息")
    @PostMapping("/addBankIssuerId")
    public BaseResponse addBankIssuerId(@RequestBody @ApiParam List<BankIssuerIdDTO> bankIssuerIdDTOList) {
        return ResultUtil.success(bankIssuerIdService.addBankIssuerId(this.getSysUserVO().getUsername(), bankIssuerIdDTOList));
    }

    @ApiOperation(value = "修改银行机构代码映射")
    @PostMapping("/updateBankIssuerId")
    public BaseResponse updateBankIssuerId(@RequestBody @ApiParam BankIssuerIdDTO bankIssuerIdDTO) {
        return ResultUtil.success(bankIssuerIdService.updateBankIssuerId(this.getSysUserVO().getUsername(), bankIssuerIdDTO));
    }

    @ApiOperation(value = "查询银行机构代码映射信息")
    @PostMapping("/pageFindBankIssuerId")
    public BaseResponse pageFindBankIssuerId(@RequestBody @ApiParam BankIssuerIdDTO bankIssuerIdDTO) {
        return ResultUtil.success(bankIssuerIdService.pageFindBankIssuerId(bankIssuerIdDTO));
    }

    @ApiOperation(value = "根据条件查询银行机构映射信息")
    @PostMapping("/getByTerm")
    public BankIssuerId getByTerm(@RequestBody @ApiParam BankIssuerIdDTO bankIssuerIdDTO) {
        return bankIssuerIdService.getByTerm(bankIssuerIdDTO);
    }

    @ApiOperation(value = "导出银行机构代码映射信息")
    @PostMapping("/exportBankIssuerId")
    public List<BankIssuerIdVO> exportBankIssuerId(@RequestBody @ApiParam BankIssuerIdDTO bankIssuerIdDTO) {
        return bankIssuerIdService.exportBankIssuerId(bankIssuerIdDTO);
    }

    @ApiOperation(value = "导入银行机构代码映射信息")
    @PostMapping("/importBankIssuerId")
    public BaseResponse importBankIssuerId(@RequestBody @ApiParam  List<BankIssuerId> bankIssuerIdList) {
        return ResultUtil.success(bankIssuerIdService.importBankIssuerId(bankIssuerIdList));
    }

}
