package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.ExportBankVO;
import com.asianwallets.permissions.feign.base.impl.BankFeignImpl;
import com.asianwallets.permissions.feign.base.impl.ChannelFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(value = "asianwallets-base", fallback = BankFeignImpl.class)
public interface BankFeign {

    @ApiOperation(value = "增加银行")
    @PostMapping("/bank/addBank")
    BaseResponse addBank(@RequestBody @ApiParam BankDTO bankDTO);

    @ApiOperation(value = "修改银行信息")
    @PostMapping("/bank/updateBank")
    BaseResponse updateBank(@RequestBody @ApiParam BankDTO bankDTO);

    @ApiOperation(value = "分页查询银行信息")
    @PostMapping("/bank/pageFindBank")
    BaseResponse pageFindBank(@RequestBody @ApiParam BankDTO bankDTO);


    @ApiOperation(value = "导出银行信息")
    @PostMapping("/bank/exportBank")
    List<ExportBankVO> exportBank(@RequestBody @ApiParam BankDTO bankDTO);

    @ApiOperation(value = "导入银行信息")
    @PostMapping("/bank/importBank")
    BaseResponse importBank(List<Bank> bankList);

    @ApiOperation(value = "根据银行名称与币种查询启用银行")
    @PostMapping("/bank/getByBankNameAndCurrency")
    Bank getByBankNameAndCurrency(BankDTO bankDTO);

    @ApiOperation(value = "查询所有银行名称")
    @PostMapping("/bank/getAllBankName")
    List<String> getAllBankName();

}
