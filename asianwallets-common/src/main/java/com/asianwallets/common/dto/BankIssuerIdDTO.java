package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "银行与银行机构代码映射输入实体", description = "银行与银行机构代码映射输入实体")
public class BankIssuerIdDTO extends BasePageHelper {

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "银行机构代码")
    private String issuerId;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    public BankIssuerIdDTO() {
    }

    public BankIssuerIdDTO(String bankName, String currency, String channelCode, String issuerId) {
        this.bankName = bankName;
        this.currency = currency;
        this.channelCode = channelCode;
        this.issuerId = issuerId;
    }
}
