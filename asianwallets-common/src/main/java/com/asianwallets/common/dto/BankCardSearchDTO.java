package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-03-07 14:40
 **/
@Data
@ApiModel(value = "银行卡查询实体", description = "银行卡查询实体")
public class BankCardSearchDTO extends BasePageHelper {

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "商户Code")
    private String merchantId;

    @ApiModelProperty(value = "银行卡id")
    private String bankCardId;

    @ApiModelProperty(value = "银行卡币种")
    private String bankCodeCurrency;

    @ApiModelProperty(value = "结算币种")
    private String bankCurrency;

    @ApiModelProperty(value = "启用禁用状态")
    private Boolean enabled;

    @ApiModelProperty(value = "是否设为默认银行卡")
    private Boolean defaultFlag;
}
