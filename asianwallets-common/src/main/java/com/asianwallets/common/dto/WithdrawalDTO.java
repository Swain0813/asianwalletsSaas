package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "提款DTO")
public class WithdrawalDTO {
    @ApiModelProperty(name = "提款银行信息List")
    List<WithdrawalBankDTO> withdrawalBankDTOS;

    @ApiModelProperty(name = "密码")
    private String tradePwd;
}
