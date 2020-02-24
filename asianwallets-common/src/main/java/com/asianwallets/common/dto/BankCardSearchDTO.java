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

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "代理商编号")
    private String agentId;

    @ApiModelProperty(value = "商户类型")
    private String merchantType;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "银行卡id")
    private String bankCardId;

    @ApiModelProperty(value = "结算币种")
    private String settleCurrency;

    @ApiModelProperty(value = "银行卡币种")
    private String bankCurrency;

    @ApiModelProperty(value = "启用禁用状态")
    private Boolean enabled;

    @ApiModelProperty(value = "是否设为默认银行卡")
    private Boolean defaultFlag;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "起始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
