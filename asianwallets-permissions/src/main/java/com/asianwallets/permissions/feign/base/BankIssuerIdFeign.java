package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.BankIssuerIdDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.BankIssuerIdVO;
import com.asianwallets.permissions.feign.base.impl.BankIssuerIdFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(value = "asianwallets-base", fallback = BankIssuerIdFeignImpl.class)
public interface BankIssuerIdFeign {

    @ApiOperation(value = "添加银行机构代码映射信息")
    @PostMapping("/bankIssuerId/addBankIssuerId")
    BaseResponse addBankIssuerId(@RequestBody @ApiParam List<BankIssuerIdDTO> bankIssuerIdDTOList);

    @ApiOperation(value = "修改银行机构代码映射")
    @PostMapping("/bankIssuerId/updateBankIssuerId")
    BaseResponse updateBankIssuerId(@RequestBody @ApiParam BankIssuerIdDTO bankIssuerIdDTO);

    @ApiOperation(value = "查询银行机构代码映射信息")
    @PostMapping("/bankIssuerId/pageFindBankIssuerId")
    BaseResponse pageFindBankIssuerId(@RequestBody @ApiParam BankIssuerIdDTO bankIssuerIdDTO);

    @ApiOperation(value = "导出银行机构代码映射信息")
    @PostMapping("/bankIssuerId/exportBankIssuerId")
    List<BankIssuerIdVO> exportBankIssuerId(@RequestBody @ApiParam BankIssuerIdDTO bankIssuerIdDTO);

}
