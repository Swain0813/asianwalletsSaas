package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "账户查询冻结与保证金信息DTO", description = "账户查询冻结与保证金信息DTO")
public class FrozenMarginInfoDTO extends BasePageHelper {

    @ApiModelProperty(value = "账户ID")
    private String accountId;

    @ApiModelProperty(value = "资金类型")//1-冻结户 2-保证金户
    private String accountType;

    @ApiModelProperty(value = "币种")
    private String currency;

}
